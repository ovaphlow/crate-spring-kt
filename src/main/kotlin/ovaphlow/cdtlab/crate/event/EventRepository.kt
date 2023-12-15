package ovaphlow.cdtlab.crate.event

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import ovaphlow.cdtlab.crate.utility.ConditionBuilder
import java.sql.ResultSet

@Repository
class EventRepository(private val jdbcTemplate: JdbcTemplate) {

    val columns = arrayOf("id", "relation_id", "reference_id", "tags", "detail", "time")
    val logger: Logger = LoggerFactory.getLogger(EventRepository::class.java)

    fun defaultFilter(
        skip: Long,
        take: Int,
        equal: List<String>,
        objectEqual: List<String>,
        arrayContain: List<String>,
        like: List<String>,
        objectLike: List<String>,
        inList: List<String>,
        lesser: List<String>,
        greater: List<String>
    ): List<Event> {
        val columnListString = columns.joinToString(", ")
        var q = "select $columnListString from events"
        val conditions = mutableListOf<String>()
        val params = mutableListOf<String>()
        val builder = ConditionBuilder()
        if (equal.isNotEmpty() && equal.size % 2 == 0) {
            builder.equalBuilder(equal, conditions, params)
        }
        if (objectEqual.isNotEmpty() && objectEqual.size % 3 == 0) {
            builder.objectContainBuilder(objectEqual, conditions, params)
        }
        if (arrayContain.isNotEmpty() && arrayContain.size % 2 == 0) {
            builder.arrayContainBuilder(arrayContain, conditions, params)
        }
        if (like.isNotEmpty() && like.size % 2 == 0) {
            builder.likeBuilder(like, conditions, params)
        }
        if (objectLike.isNotEmpty() && objectLike.size % 3 == 0) {
            builder.objectLikeBuilder(objectLike, conditions, params)
        }
        if (inList.isNotEmpty() && inList.size > 1) {
            builder.inBuilder(inList, conditions, params)
        }
        if (lesser.isNotEmpty() && lesser.size % 2 == 0) {
            builder.lesserBuilder(lesser, conditions, params)
        }
        if (greater.isNotEmpty() && greater.size % 2 == 0) {
            builder.greaterBuilder(greater, conditions, params)
        }
        if (conditions.size > 0) {
            q += conditions.joinToString(" and ", " where ")
        }
        q += " order by id desc limit $skip, $take"
        logger.info(q)
        val result = jdbcTemplate.query(q, { rs: ResultSet, _: Int ->
            Event(
                id = rs.getLong("id"),
                relationId = rs.getLong("relation_id"),
                referenceId = rs.getLong("reference_id"),
                tags = rs.getString("tags"),
                detail = rs.getString("detail"),
                time = rs.getDate("time")
            )
        }, *params.toTypedArray())
        return result
    }

}
