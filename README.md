## iSplash项目首个版本技术分享

![sample-of-livedata](asset/iSplash-version-1.gif)

技术栈 : LifeCycle + LiveData + ViewModel + Coroutine + Navigation + Kotlin

### 1. 一个完整的网络加载流程是怎样的？
以拿取最新照片列表为例
1. MainActivity -> MainFragment -> LatestPhotosFragment
2. 在LatestPhotosFragment中创建LatestPhotoViewModel实例，通过LatestPhotoViewModel实例内部的LiveData观察数据变化，只要数据一更新UI就能即时被刷新
3. obtainLatestPhotoList函数内部就涉及到了LiveData、ViewModel、Coroutine的结合
    * LiveData与ViewModel的结合：在ViewModel中使用LiveData包装数据，达到实时监控数据变化，实时更新UI的目的
    * ViewModel与Coroutine的结合：在ViewModel中访问suspend函数，并将结果更新到LiveData数据中

### 2. MVVM架构图
1. 常规mvvm
![常规mvvm](https://upload.wikimedia.org/wikipedia/commons/thumb/8/87/MVVMPattern.png/660px-MVVMPattern.png)

2. Android下推荐的mvvm结构

![mvvm](https://developer.android.com/topic/libraries/architecture/images/final-architecture.png?hl=zh-cn)

3. mvvm优劣势
    * 优势：解耦更彻底，不会像mvc一样造成Activity代码量巨大，也不会像mvp一样出现大量的presenter与view层的交互接口。双向绑定技术使得ViewModel更加关注业务逻辑的处理，而不需要去关心ui的刷新
    * 劣势：数据绑定使得bug很难被调试，双向绑定技术不利于代码重用

### 3. LifeCycle
1. 官网文档链接：https://developer.android.com/topic/libraries/architecture/lifecycle?hl=zh-cn
2. 是什么？
    * 生命周期感知型组件，可感知另一个组件（如Activity和Fragment）的生命周期状态变化，并执行相应操作来做出响应
3. 为什么？
    * 传统模式下，过多代码放在生命周期方法中会导致Activity和Fragment中生命周期方法臃肿难以维护，条理性变差且不利于阅读
    * 通过LifeCycle，我们可以将代码从生命周期方法移到组件本身中，针对不同场景可以注册多个观察者，让同一场景的事件处于同一观察者中，这样更便于后续维护，同时提示代码的阅读性，让代码相对优雅
4. 怎么做？
    * implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version")
    * 生命周期拥有者LifecycleOwner，即Activity和Fragment，ComponentActivity和Fragment都已实现了LifecycleOwner
    * 生命周期观察者LifecycleObserver，可以是任意类，常见的有MVP中的p，自定义view等。主要分两步，首先创建一个Observer，然后添加到LifeCycle中
      ```java
      class MyObserver : LifecycleObserver {

          @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
          fun connectListener() {
              ...
          }

          @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
          fun disconnectListener() {
              ...
          }
      }

      myLifecycleOwner.getLifecycle().addObserver(MyObserver())
      ```
5. 原理是什么？
    * LifeCycle组件实际上并没有带来什么新的功能，它通过**观察者模式+注解**来让我们更方便的监听Activity和Fragment的生命周期变化
    * 事件源：FragmentActivity中mFragmentLifecycleRegistry实例会在每个生命周期方法中调起相应事件
    * 调用链：**FragmentActivity** - **mFragmentLifecycleRegistry** - 相应生命周期方法 - handleLifecycleEvent - **LifecycleRegistry|forwardPass** - dispatchEvent - mLifecycleObserver.onStateChanged
      ```java
      ➡️ FragmentActivity
      @Override
      protected void onCreate(@Nullable Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);

          mFragmentLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
          mFragments.dispatchCreate();
      }

      ➡️ LifecycleRegistry
      public void handleLifecycleEvent(@NonNull Lifecycle.Event event) {
          enforceMainThreadIfNeeded("handleLifecycleEvent");
          moveToState(event.getTargetState());
      }

      ➡️ LifecycleRegistry
      private void sync() {
          LifecycleOwner lifecycleOwner = mLifecycleOwner.get();
          if (lifecycleOwner == null) {
              throw new IllegalStateException("LifecycleOwner of this LifecycleRegistry is already"
                      + "garbage collected. It is too late to change lifecycle state.");
          }
          while (!isSynced()) {
              mNewEventOccurred = false;
              if (mState.compareTo(mObserverMap.eldest().getValue().mState) < 0) {
                  backwardPass(lifecycleOwner);
              }
              Map.Entry<LifecycleObserver, ObserverWithState> newest = mObserverMap.newest();
              if (!mNewEventOccurred && newest != null
                      && mState.compareTo(newest.getValue().mState) > 0) {
                  forwardPass(lifecycleOwner);
              }
          }
          mNewEventOccurred = false;
      }

      ➡️ ObserverWithState
      void dispatchEvent(LifecycleOwner owner, Event event) {
          State newState = event.getTargetState();
          mState = min(mState, newState);
          mLifecycleObserver.onStateChanged(owner, event);
          mState = newState;
      }
      ```
    * 回调链：回调相对简单，就是在LifecycleObserver的实现中调起相应事件来处理相应代码逻辑
      ```java
      lifecycle.addObserver(object : LifecycleObserver {
          @OnLifecycleEvent(Lifecycle.Event.ON_START)
          fun onStart() {
              d("onStart")
          }

          @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
          fun onResume() {
              d("onResume")
          }

          @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
          fun onStop() {
              d("onStop")
          }

          @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
          fun onDestroy() {
              d("onDestroy")
          }
      })
      ```

### 4. LiveData
  * 官网文档链接：https://developer.android.com/topic/libraries/architecture/livedata?hl=zh-cn

  * 是什么？

    LiveData是具有生命周期感知能力的数据存储器类，能遵循其他应用组件（如 Activity、Fragment 或 Service）的生命周期，而这种感知能力可确保LiveData仅更新处于活跃生命周期状态的应用组件观察者。

  * 为什么？

     * 因为界面状态随时变化，每当这种情况出现时，LiveData会完成数据的更新。
     * 因为绑定了LifeCycle关联了生命周期，在生命周期组件销毁后观察者会进行自我清理。同时如果观察者的生命周期处于非活跃状态，则它不会接收任何LiveData事件，不会因为Activity停止而导致崩溃。

  * 怎么做？

     * implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
     * 通过LiveData setValue和postValue 更新数据
     * 通过observe(LifecycleOwner owner, Observer<? super T> observer)观察数据变化，就是这个owner使得LiveData可以感知到生命周期
     * 可以单独使用LiveData，但更常用的做法是结合ViewModel；可以利用Transformations将LiveData数据【展开 - map】或者【转换 - switchMap】；可以利用MediatorLiveData合并多个LiveData源，任一源变化都会出发数据更新回调；最后可以结合kt协程来处理耗时任务。详情参考 [SampleLiveData](https://github.com/Leeeyou/SampleOfLivedata.git)

  * 原理是什么？

     * 观察者模式+LifeCycle，内部通过SafeIterableMap集合维护了观察者列表，当有数据更新时，会遍历map集合通知观察者回调其onChanged函数
     * 注册观察者：LiveData|observe
        ```java
        ➡️ LiveData
        private SafeIterableMap<Observer<? super T>, ObserverWrapper> mObservers =
            new SafeIterableMap<>();
        
        ...

        @MainThread
        public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
            assertMainThread("observe");
            if (owner.getLifecycle().getCurrentState() == DESTROYED) {
                // ignore
                return;
            }
            LifecycleBoundObserver wrapper = new LifecycleBoundObserver(owner, observer);
            ObserverWrapper existing = mObservers.putIfAbsent(observer, wrapper);
            if (existing != null && !existing.isAttachedTo(owner)) {
                throw new IllegalArgumentException("Cannot add the same observer"
                        + " with different lifecycles");
            }
            if (existing != null) {
                return;
            }
            owner.getLifecycle().addObserver(wrapper);
        }
        ```
     * 更新数据：LiveData|setValue|dispatchingValue|considerNotify
        ```java
        ➡️ LiveData
        @MainThread
        protected void setValue(T value) {
            assertMainThread("setValue");
            mVersion++;
            mData = value;
            dispatchingValue(null);
        }

        void dispatchingValue(@Nullable ObserverWrapper initiator) {
            if (mDispatchingValue) {
                mDispatchInvalidated = true;
                return;
            }
            mDispatchingValue = true;
            do {
                mDispatchInvalidated = false;
                if (initiator != null) {
                    considerNotify(initiator);
                    initiator = null;
                } else {
                    for (Iterator<Map.Entry<Observer<? super T>, ObserverWrapper>> iterator =
                            mObservers.iteratorWithAdditions(); iterator.hasNext(); ) {
                        considerNotify(iterator.next().getValue());
                        if (mDispatchInvalidated) {
                            break;
                        }
                    }
                }
            } while (mDispatchInvalidated);
            mDispatchingValue = false;
        }

        private void considerNotify(ObserverWrapper observer) {
            if (!observer.mActive) {
                return;
            }
            if (!observer.shouldBeActive()) {
                observer.activeStateChanged(false);
                return;
            }
            if (observer.mLastVersion >= mVersion) {
                return;
            }
            observer.mLastVersion = mVersion;
            observer.mObserver.onChanged((T) mData);
        }
        ```

### 5. ViewModel
   * 官网文档链接：https://developer.android.com/topic/libraries/architecture/viewmodel?hl=zh-cn
   * 是什么？

      以注重生命周期的方式存储和管理界面相关的数据

   * 为什么？

     * 为Activity和Fragment减负，将跟界面相关的数据全部委托给ViewModel来负责。典型应用场景是屏幕旋转，耗时任务异步回调
     * 接管界面控制器中（Activity、Fragment），分离出视图数据所有权，使得界面数据的管理更加容易和高效

   * 怎么做？

      * implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")

      * 怎么创建？
        * 便捷方式：private val viewModel: LatestPhotoViewModel by viewModels()
        * 最终通过：ViewModelProvider(store, factory).get(viewModelClass.java)

      * 实操：实现一个定时器，在页面横竖屏切换时保留数值并继续计时，详情参考[SampleLiveData](https://github.com/Leeeyou/SampleOfLivedata/blob/master/app/src/main/java/com/leeeyou123/samplelivedata/ViewModelActivity.kt)

      * 将Kotlin协程与架构组件一起使用

        * 对于ViewModelScope，使用androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0或更高版本
          * 为应用中的每个ViewModel定义了ViewModelScope。如果ViewModel已清除，则在此范围内启动的协程都会自动取消
        * 对于LifecycleScope，使用androidx.lifecycle:lifecycle-runtime-ktx:2.2.0或更高版本
          * 为每个Lifecycle对象定义了LifecycleScope。在此范围内启动的协程会在Lifecycle被销毁时取消

   * 原理是什么？
     * 首先ViewModel生命周期
     ![ViewModel生命周期](https://developer.android.com/images/topic/libraries/architecture/viewmodel-lifecycle.png?hl=zh_cn)
     * 存储原理
       * 创建ViewModel的流程上面已经提到过是通过ViewModelProvider | get，ViewModel则保存在ViewModelStore的mMap集合中，key是canonicalName，value是ViewModel实例，ViewModelStore是ViewModel的缓存管理者
       ```java
        ➡️ ViewModelProvider
        @MainThread
        public <T extends ViewModel> T get(@NonNull String key, @NonNull Class<T> modelClass) {
            ViewModel viewModel = mViewModelStore.get(key);

            if (modelClass.isInstance(viewModel)) {
                if (mFactory instanceof OnRequeryFactory) {
                    ((OnRequeryFactory) mFactory).onRequery(viewModel);
                }
                return (T) viewModel;
            } else {
                //noinspection StatementWithEmptyBody
                if (viewModel != null) {
                    // TODO: log a warning.
                }
            }
            if (mFactory instanceof KeyedFactory) {
                viewModel = ((KeyedFactory) mFactory).create(key, modelClass);
            } else {
                viewModel = mFactory.create(modelClass);
            }
            mViewModelStore.put(key, viewModel);
            return (T) viewModel;
        }
       ```
       * ViewModelStore实例化的切入点：ComponentActivity的构造函数中会创建ViewModelStore实例
       ```java
        ➡️ ComponentActivity
        public ComponentActivity() {
          ...
          getLifecycle().addObserver(new LifecycleEventObserver() {
              @Override
              public void onStateChanged(@NonNull LifecycleOwner source,
                      @NonNull Lifecycle.Event event) {
                  ensureViewModelStore();
                  getLifecycle().removeObserver(this);
              }
          });
        }
       ```
     * 销毁原理
       * 从上层来看，对于ViewModel是回调onCleared函数进行销毁
        ```java
        ➡️ ViewModel
        protected void onCleared() {
        }

        @MainThread
        final void clear() {
            mCleared = true;
            if (mBagOfTags != null) {
                synchronized (mBagOfTags) {
                    for (Object value : mBagOfTags.values()) {
                        closeWithRuntimeException(value);
                    }
                }
            }
            onCleared();
        }
        ```
       * 从内部来看，实际上是利用Lifecycle监听ON_DESTROY状态，然后调用ViewModelStore的clear方法将mMap清空
       ```java
        ➡️ ComponentActivity
        getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source,
                    @NonNull Lifecycle.Event event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    // Clear out the available context
                    mContextAwareHelper.clearAvailableContext();
                    // And clear the ViewModelStore
                    if (!isChangingConfigurations()) {
                        getViewModelStore().clear();
                    }
                }
            }
        });

        ➡️ ViewModelStore
        private final HashMap<String, ViewModel> mMap = new HashMap<>();

        public final void clear() {
            for (ViewModel vm : mMap.values()) {
                vm.clear();
            }
            mMap.clear();
        }
       ```