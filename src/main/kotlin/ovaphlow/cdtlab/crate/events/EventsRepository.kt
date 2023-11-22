package ovaphlow.cdtlab.crate.events

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class EventsRepository(private val jdbcTemplate: JdbcTemplate) {
    val columns = arrayOf("id", "relation_id", "reference_id", "tags", "detail", "time")

    fun filter(
        skip: Long,
        take: Int,
        relationId: Long,
        referenceId: Long,
        tags: List<String>,
        detail: Map<String, Any>,
        timeRange: List<String>
    ): List<Map<String, Any>> {
        val columnListString = columns.joinToString(", ", "cast(id as char) _id, ")
        var q = "select $columnListString from events"
        val conditions = mutableListOf<String>()
        val params = mutableListOf<Any>()
        if (relationId > 0) {
            conditions += "relation_id = ?"
            params += relationId
        }
        if (referenceId > 0) {
            conditions += "reference_id = ?"
            params += referenceId
        }
        tags.forEach {
            conditions += "json_contains(tags, json_array(?))"
            params += it
        }
        detail.forEach { (key, value) ->
            conditions += "json_contains(detail, json_object(?, ?))"
            params += key
            params += value
        }
        if (timeRange.size == 2) {
            conditions += "time >= ?"
            conditions += "time <= ?"
            params += timeRange[0]
            params += timeRange[1]
        }
        if (conditions.size > 0) {
            q = "$q where ${conditions.joinToString(" and ")}"
        }
        q = "$q order by id desc limit $skip, $take"
        if (params.size == 0) {
            return jdbcTemplate.queryForList(q)
        }
        return jdbcTemplate.queryForList(q, *params.toTypedArray())
    }
}
