package com.billding.zookeeper

import kafka.utils.ZkUtils

class ZookeeperConfig {
  val sessionTimeoutMs = 10 * 1000
  val connectionTimeoutMs = 8 * 1000
  val port = 2181

  val zkUtils: ZkUtils = ZkUtils.apply(
    s"localhost:$port",
    sessionTimeoutMs,
    connectionTimeoutMs,
    false
  )

}
