package org.oppia.android.testing.threading

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import org.oppia.android.testing.robolectric.IsOnRobolectric
import org.oppia.android.util.threading.BackgroundDispatcher
import org.oppia.android.util.threading.BlockingDispatcher
import java.util.concurrent.Executors
import javax.inject.Provider
import javax.inject.Singleton

/**
 * Dagger [Module] that provides [CoroutineDispatcher]s that bind to [BackgroundDispatcher] and
 * [BlockingDispatcher] qualifiers.
 */
@Module
class TestDispatcherModule {
  @Provides
  @BackgroundDispatcher
  fun provideBackgroundDispatcher(
    @BackgroundTestDispatcher testCoroutineDispatcher: TestCoroutineDispatcher
  ): CoroutineDispatcher = testCoroutineDispatcher

  @Provides
  @BlockingDispatcher
  fun provideBlockingDispatcher(
    @BlockingTestDispatcher testCoroutineDispatcher: TestCoroutineDispatcher
  ): CoroutineDispatcher = testCoroutineDispatcher

  @Provides
  @BackgroundTestDispatcher
  @Singleton
  fun provideBackgroundTestDispatcher(
    factory: TestCoroutineDispatcher.Factory
  ): TestCoroutineDispatcher {
    return factory.createDispatcher(
      Executors.newFixedThreadPool(/* nThreads = */ 4).asCoroutineDispatcher()
    )
  }

  @Provides
  @BlockingTestDispatcher
  @Singleton
  fun provideBlockingTestDispatcher(
    factory: TestCoroutineDispatcher.Factory
  ): TestCoroutineDispatcher {
    return factory.createDispatcher(Executors.newSingleThreadExecutor().asCoroutineDispatcher())
  }

  @Provides
  fun provideTestCoroutineDispatchers(
    @IsOnRobolectric isOnRobolectric: Boolean,
    robolectricImplProvider: Provider<TestCoroutineDispatchersRobolectricImpl>,
    espressoImplProvider: Provider<TestCoroutineDispatchersEspressoImpl>
  ): TestCoroutineDispatchers {
    return if (isOnRobolectric) robolectricImplProvider.get() else espressoImplProvider.get()
  }

  @Provides
  fun provideTestCoroutineDispatcherFactory(
    @IsOnRobolectric isOnRobolectric: Boolean,
    robolectricFactoryProvider: Provider<TestCoroutineDispatcherRobolectricImpl.FactoryImpl>,
    espressoFactoryProvider: Provider<TestCoroutineDispatcherEspressoImpl.FactoryImpl>
  ): TestCoroutineDispatcher.Factory {
    return if (isOnRobolectric) robolectricFactoryProvider.get() else espressoFactoryProvider.get()
  }
}
