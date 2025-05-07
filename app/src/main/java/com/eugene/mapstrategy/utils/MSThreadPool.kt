package com.eugene.mapstrategy.utils

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author EugeneYu
 * @date 2025/5/7
 * @desc 线程池单例
 */
object MSThreadPool  {

  private const val CORE_POOL_SIZE = 5
  private const val MAXIMUM_POOL_SIZE = 10
  private const val KEEP_ALIVE_TIME = 1L
  private val threadPoolExecutor: ThreadPoolExecutor = ThreadPoolExecutor(
    CORE_POOL_SIZE,
    MAXIMUM_POOL_SIZE,
    KEEP_ALIVE_TIME,
    TimeUnit.MILLISECONDS,
    LinkedBlockingQueue(),
    LJThreadFactory(),
    ThreadPoolExecutor.DiscardPolicy()
  )

  class LJThreadFactory internal constructor() : AtomicInteger(), ThreadFactory {
    override fun newThread(runnable: Runnable): Thread {
      val thread = Thread(runnable, "MSThreadPool-" + this.getAndIncrement())
      thread.priority = 5
      thread.isDaemon = true
      return thread
    }

    override fun toByte(): Byte {
      TODO("Not yet implemented")
    }

    override fun toShort(): Short {
      TODO("Not yet implemented")
    }
  }

  fun post(task: Runnable) {
    try {
      threadPoolExecutor.execute(task)
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  fun shutdown() {
    try {
      threadPoolExecutor.shutdown()
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  fun shutdownNow(): List<Runnable> {
    return try {
      threadPoolExecutor.shutdownNow()
    } catch (e: Exception) {
      e.printStackTrace()
      emptyList()
    }
  }

  fun isShutdown(): Boolean {
    try {
      return threadPoolExecutor.isShutdown
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return false
  }

  fun isTerminated(): Boolean {
    return try {
      threadPoolExecutor.isTerminated
    } catch (e: Exception) {
      e.printStackTrace()
      false
    }
  }

  fun awaitTermination(timeout: Long, unit: TimeUnit): Boolean {
    return try {
      threadPoolExecutor.awaitTermination(timeout, unit)
    } catch (e: Exception) {
      e.printStackTrace()
      false
    }
  }

  fun getActiveCount(): Int {
    return try {
      threadPoolExecutor.activeCount
    } catch (e: Exception) {
      e.printStackTrace()
      0
    }
  }

  fun getQueueSize(): Int {
    return try {
      threadPoolExecutor.queue.size
    } catch (e: Exception) {
      e.printStackTrace()
      0
    }
  }
}