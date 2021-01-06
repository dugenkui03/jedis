package redis.clients.jedis.commands;

import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.BitPosParams;
import redis.clients.jedis.StreamConsumersInfo;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.GeoUnit;
import redis.clients.jedis.ListPosition;
import redis.clients.jedis.StreamGroupInfo;
import redis.clients.jedis.StreamInfo;
import redis.clients.jedis.StreamPendingEntry;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.StreamEntry;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.params.GeoRadiusParam;
import redis.clients.jedis.params.SetParams;
import redis.clients.jedis.params.ZAddParams;
import redis.clients.jedis.params.ZIncrByParams;
import redis.clients.jedis.params.LPosParams;

/**
 * Common interface for sharded and non-sharded Jedis
 */
public interface JedisCommands {
  /**
   * 为指定key设置值、如果已经存在则覆盖
   */
  String set(String key, String value);

  // todo
  String set(String key, String value, SetParams params);

  // 获取指定key的value
  String get(String key);

  // 指定key是否存在
  Boolean exists(String key);

  /**
   * 持久化指定key
   */
  Long persist(String key);

  /**
   * 返回key的类型
   *    none (key不存在)
   *    string (字符串)
   *    list (列表)
   *    set (集合)
   *    zset (有序集)
   *    hash (哈希表)
   */
  String type(String key);

  // https://www.runoob.com/redis/keys-dump.html
  // 序列化指定的key
  byte[] dump(String key);

  // todo
  String restore(String key, int ttl, byte[] serializedValue);

  String restoreReplace(String key, int ttl, byte[] serializedValue);

  // 设置指定key的过期时间、单位 秒
  Long expire(String key, int seconds);

  // 设置指定key的过期时间、单位 毫秒
  Long pexpire(String key, long milliseconds);

  // 设置指定过期时间戳，成功则1、失败则0。单位是秒
  // todo 如果unixTime早于当前时间呢
  Long expireAt(String key, long unixTime);

  // 同上、单位是毫秒、13位时间戳
  Long pexpireAt(String key, long millisecondsTimestamp);

  // 返回该key存活的 秒 数
  Long ttl(String key);

  // 返回该key存活的 毫秒 数
  Long pttl(String key);

  // 返回指定 key 的最后访问时间
  Long touch(String key);

  Boolean setbit(String key, long offset, boolean value);

  Boolean setbit(String key, long offset, String value);

  Boolean getbit(String key, long offset);

  Long setrange(String key, long offset, String value);

  String getrange(String key, long startOffset, long endOffset);

  String getSet(String key, String value);

  Long setnx(String key, String value);

  String setex(String key, int seconds, String value);

  String psetex(String key, long milliseconds, String value);

  Long decrBy(String key, long decrement);

  Long decr(String key);

  Long incrBy(String key, long increment);

  Double incrByFloat(String key, double increment);

  Long incr(String key);

  Long append(String key, String value);

  String substr(String key, int start, int end);

  Long hset(String key, String field, String value);

  Long hset(String key, Map<String, String> hash);

  String hget(String key, String field);

  Long hsetnx(String key, String field, String value);

  String hmset(String key, Map<String, String> hash);

  List<String> hmget(String key, String... fields);

  Long hincrBy(String key, String field, long value);

  Double hincrByFloat(String key, String field, double value);

  Boolean hexists(String key, String field);

  Long hdel(String key, String... field);

  Long hlen(String key);

  Set<String> hkeys(String key);

  List<String> hvals(String key);

  Map<String, String> hgetAll(String key);

  Long rpush(String key, String... string);


  /**
   * https://www.runoob.com/redis/redis-lists.html
   *
   * ======================================== list列表 操作 ===================================================
   */

  // 插入元素
  Long lpush(String key, String... string);

  // 列表长度
  Long llen(String key);

  /**
   * 获取指定下标的元素、从0开始。-1表示倒数第一个元素。
   * 前闭后闭、所以[0,0]是取第一个元素。[0,-1]表示获取所有的元素。
   */
  List<String> lrange(String key, long start, long stop);

  /**
   * 裁剪元素列表，只保留指定区间的元素
   */
  String ltrim(String key, long start, long stop);

  // 获取指定索引的元素、从0开始，可为负数
  String lindex(String key, long index);

  // 设置指定下标的值
  String lset(String key, long index, String value);

  /**
   * 移除key中和value相等的元素。
   *
   * @param count
   *        1. count>0: 从表头开始、移除count个和value相等的元素；
   *        2. count<0: 从表尾开始、移除|count|个和value相等的元素；
   *        3. count=0: 移除list中所有和value相等的元素。
   */
  Long lrem(String key, long count, String value);

  /**
   * 移除并返回列表的第一个元素、即最先放入的元素。
   */
  String lpop(String key);

  /**
   *  移除并返回列表的最后一个元素，即最后放入的元素
   */
  String rpop(String key);

  /**
   * lpos：Available since 6.0.6.
   *
   * https://redis.io/commands/lpos
   */
  Long lpos(String key, String element);

  Long lpos(String key, String element, LPosParams params);

  List<Long> lpos(String key, String element, LPosParams params, long count);


  /**
   * https://www.runoob.com/redis/redis-sets.html
   *
   * ======================================== set集合 操作 ===================================================
   */

  // 添加元素
  Long sadd(String key, String... member);

  // 查看集合中所有元素
  Set<String> smembers(String key);

  /**
   * 如果key不是集合类型、则抛出异常
   *
   * 如果元素不存在则忽略；返回移除的元素个数。
   */
  Long srem(String key, String... member);

  // 随机移除集合中的一个元素并返回
  String spop(String key);

  // 随机移除元素中的多个元素并返回
  // 如果count > size(key)，则返回集合中所有的元素
  Set<String> spop(String key, long count);

  Long scard(String key);

  Boolean sismember(String key, String member);

  List<Boolean> smismember(String key, String... members);

  String srandmember(String key);

  List<String> srandmember(String key, int count);

  Long strlen(String key);

  Long zadd(String key, double score, String member);

  Long zadd(String key, double score, String member, ZAddParams params);

  Long zadd(String key, Map<String, Double> scoreMembers);

  Long zadd(String key, Map<String, Double> scoreMembers, ZAddParams params);

  Set<String> zrange(String key, long start, long stop);

  Long zrem(String key, String... members);

  Double zincrby(String key, double increment, String member);

  Double zincrby(String key, double increment, String member, ZIncrByParams params);

  Long zrank(String key, String member);

  Long zrevrank(String key, String member);

  Set<String> zrevrange(String key, long start, long stop);

  Set<Tuple> zrangeWithScores(String key, long start, long stop);

  Set<Tuple> zrevrangeWithScores(String key, long start, long stop);

  Long zcard(String key);

  Double zscore(String key, String member);

  List<Double> zmscore(String key, String... members);

  Tuple zpopmax(String key);

  Set<Tuple> zpopmax(String key, int count);

  Tuple zpopmin(String key);

  Set<Tuple> zpopmin(String key, int count);

  List<String> sort(String key);

  List<String> sort(String key, SortingParams sortingParameters);

  Long zcount(String key, double min, double max);

  Long zcount(String key, String min, String max);

  Set<String> zrangeByScore(String key, double min, double max);

  Set<String> zrangeByScore(String key, String min, String max);

  Set<String> zrevrangeByScore(String key, double max, double min);

  Set<String> zrangeByScore(String key, double min, double max, int offset, int count);

  Set<String> zrevrangeByScore(String key, String max, String min);

  Set<String> zrangeByScore(String key, String min, String max, int offset, int count);

  Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count);

  Set<Tuple> zrangeByScoreWithScores(String key, double min, double max);

  Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min);

  Set<Tuple> zrangeByScoreWithScores(String key, double min, double max, int offset, int count);

  Set<String> zrevrangeByScore(String key, String max, String min, int offset, int count);

  Set<Tuple> zrangeByScoreWithScores(String key, String min, String max);

  Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min);

  Set<Tuple> zrangeByScoreWithScores(String key, String min, String max, int offset, int count);

  Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min, int offset, int count);

  Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min, int offset, int count);

  Long zremrangeByRank(String key, long start, long stop);

  Long zremrangeByScore(String key, double min, double max);

  Long zremrangeByScore(String key, String min, String max);

  Long zlexcount(String key, String min, String max);

  Set<String> zrangeByLex(String key, String min, String max);

  Set<String> zrangeByLex(String key, String min, String max, int offset,
      int count);

  Set<String> zrevrangeByLex(String key, String max, String min);

  Set<String> zrevrangeByLex(String key, String max, String min,
      int offset, int count);

  Long zremrangeByLex(String key, String min, String max);

  Long linsert(String key, ListPosition where, String pivot, String value);

  Long lpushx(String key, String... string);

  Long rpushx(String key, String... string);

  List<String> blpop(int timeout, String key);

  List<String> brpop(int timeout, String key);

  Long del(String key);

  Long unlink(String key);

  String echo(String string);

  Long move(String key, int dbIndex);

  Long bitcount(String key);

  Long bitcount(String key, long start, long end);

  Long bitpos(String key, boolean value);

  Long bitpos(String key, boolean value, BitPosParams params);

  ScanResult<Map.Entry<String, String>> hscan(String key, String cursor);

  ScanResult<Map.Entry<String, String>> hscan(String key, String cursor,
      ScanParams params);

  ScanResult<String> sscan(String key, String cursor);

  ScanResult<Tuple> zscan(String key, String cursor);

  ScanResult<Tuple> zscan(String key, String cursor, ScanParams params);

  ScanResult<String> sscan(String key, String cursor, ScanParams params);

  Long pfadd(String key, String... elements);

  long pfcount(String key);

  // Geo Commands

  Long geoadd(String key, double longitude, double latitude, String member);

  Long geoadd(String key, Map<String, GeoCoordinate> memberCoordinateMap);

  Double geodist(String key, String member1, String member2);

  Double geodist(String key, String member1, String member2, GeoUnit unit);

  List<String> geohash(String key, String... members);

  List<GeoCoordinate> geopos(String key, String... members);

  List<GeoRadiusResponse> georadius(String key, double longitude, double latitude, double radius,
      GeoUnit unit);

  List<GeoRadiusResponse> georadiusReadonly(String key, double longitude, double latitude, double radius,
      GeoUnit unit);

  List<GeoRadiusResponse> georadius(String key, double longitude, double latitude, double radius,
      GeoUnit unit, GeoRadiusParam param);

  List<GeoRadiusResponse> georadiusReadonly(String key, double longitude, double latitude, double radius,
      GeoUnit unit, GeoRadiusParam param);

  List<GeoRadiusResponse> georadiusByMember(String key, String member, double radius, GeoUnit unit);

  List<GeoRadiusResponse> georadiusByMemberReadonly(String key, String member, double radius, GeoUnit unit);

  List<GeoRadiusResponse> georadiusByMember(String key, String member, double radius, GeoUnit unit,
      GeoRadiusParam param);

  List<GeoRadiusResponse> georadiusByMemberReadonly(String key, String member, double radius, GeoUnit unit,
      GeoRadiusParam param);

  /**
   * Executes BITFIELD Redis command
   * @param key
   * @param arguments
   * @return 
   */
  List<Long> bitfield(String key, String...arguments);

  List<Long> bitfieldReadonly(String key, String...arguments);

  /**
   * Used for HSTRLEN Redis command
   * @param key 
   * @param field
   * @return length of the value for key
   */
  Long hstrlen(String key, String field);

  /**
   * XADD key ID field string [field string ...]
   * 
   * @param key
   * @param id
   * @param hash
   * @return the ID of the added entry
   */
  StreamEntryID xadd(String key, StreamEntryID id, Map<String, String> hash);

  /**
   * XADD key MAXLEN ~ LEN ID field string [field string ...]
   * 
   * @param key
   * @param id
   * @param hash
   * @param maxLen
   * @param approximateLength
   * @return
   */
  StreamEntryID xadd(String key, StreamEntryID id, Map<String, String> hash, long maxLen, boolean approximateLength);
  
  /**
   * XLEN key
   * 
   * @param key
   * @return
   */
  Long xlen(String key);

  /**
   * XRANGE key start end [COUNT count]
   * 
   * @param key
   * @param start minimum {@link StreamEntryID} for the retrieved range, passing <code>null</code> will indicate minimum ID possible in the stream  
   * @param end maximum {@link StreamEntryID} for the retrieved range, passing <code>null</code> will indicate maximum ID possible in the stream
   * @param count maximum number of entries returned 
   * @return The entries with IDs matching the specified range. 
   */
  List<StreamEntry> xrange(String key, StreamEntryID start, StreamEntryID end, int count);

  /**
   * XREVRANGE key end start [COUNT <n>]
   * 
   * @param key
   * @param start minimum {@link StreamEntryID} for the retrieved range, passing <code>null</code> will indicate minimum ID possible in the stream  
   * @param end maximum {@link StreamEntryID} for the retrieved range, passing <code>null</code> will indicate maximum ID possible in the stream
   * @param count The entries with IDs matching the specified range. 
   * @return the entries with IDs matching the specified range, from the higher ID to the lower ID matching.
   */
  List<StreamEntry> xrevrange(String key, StreamEntryID end, StreamEntryID start, int count);
    
  /**
   * XACK key group ID [ID ...]
   * 
   * @param key
   * @param group
   * @param ids
   * @return
   */
  long xack(String key, String group,  StreamEntryID... ids);
  
  /**
   * XGROUP CREATE <key> <groupname> <id or $>
   * 
   * @param key
   * @param groupname
   * @param id
   * @param makeStream
   * @return
   */
  String xgroupCreate( String key, String groupname, StreamEntryID id, boolean makeStream);
  
  /**
   * XGROUP SETID <key> <groupname> <id or $>
   * 
   * @param key
   * @param groupname
   * @param id
   * @return
   */
  String xgroupSetID( String key, String groupname, StreamEntryID id);
  
  /**
   * XGROUP DESTROY <key> <groupname>
   * 
   * @param key
   * @param groupname
   * @return
   */
  long xgroupDestroy( String key, String groupname);
  
  /**
   * XGROUP DELCONSUMER <key> <groupname> <consumername> 
   * @param key
   * @param groupname
   * @param consumername
   * @return
   */
  Long xgroupDelConsumer( String key, String groupname, String consumername);

  /**
   * XPENDING key group [start end count] [consumer]
   * 
   * @param key
   * @param groupname
   * @param start
   * @param end
   * @param count
   * @param consumername
   * @return
   */
  List<StreamPendingEntry> xpending(String key, String groupname, StreamEntryID start, StreamEntryID end, int count, String consumername);
  
  /**
   * XDEL key ID [ID ...]
   * @param key
   * @param ids
   * @return
   */
  long xdel( String key, StreamEntryID... ids);
  
  /**
   * XTRIM key MAXLEN [~] count
   * @param key
   * @param maxLen
   * @param approximate
   * @return
   */
  long xtrim( String key, long maxLen, boolean approximate);
 
  /**
   *  XCLAIM <key> <group> <consumer> <min-idle-time> <ID-1> <ID-2>
   *        [IDLE <milliseconds>] [TIME <mstime>] [RETRYCOUNT <count>]
   *        [FORCE] [JUSTID]
   */        
  List<StreamEntry> xclaim( String key, String group, String consumername, long minIdleTime, 
      long newIdleTime, int retries, boolean force, StreamEntryID... ids);

  /**
   * Introspection command used in order to retrieve different information about the stream
   * @param key Stream name
   * @return {@link StreamInfo} that contains information about the stream
   */
  StreamInfo xinfoStream (String key);

  /**
   * Introspection command used in order to retrieve different information about groups in the stream
   * @param key Stream name
   * @return List of {@link StreamGroupInfo} containing information about groups
   */
  List<StreamGroupInfo> xinfoGroup (String key);

  /**
   * Introspection command used in order to retrieve different information about consumers in the group
   * @param key Stream name
   * @param group Group name
   * @return List of {@link StreamConsumersInfo} containing information about consumers that belong
   * to the the group
   */
  List<StreamConsumersInfo> xinfoConsumers (String key, String group);
}
