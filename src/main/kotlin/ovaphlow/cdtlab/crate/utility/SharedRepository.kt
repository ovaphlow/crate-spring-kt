package ovaphlow.cdtlab.crate.utility

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class SharedRepository(private val jdbcTemplate: JdbcTemplate) {

    var logger: Logger = LoggerFactory.getLogger(SharedRepository::class.java)

    val schemas = mapOf(
        "event" to mapOf(
            "table" to "events",
            "columns" to listOf("id", "relation_id", "reference_id", "tags", "detail", "time"),
        ),
    )

    fun retrieve(name: String, skip: Long, take: Int, option: Map<String, List<String>>): List<Map<String, Any>> {
        val columns = schemas[name]?.get("columns") as List<*>
        var q = """
        select ${columns.joinToString(separator = ", ")}
        from ${schemas[name]?.get("table")}
        """.trimIndent()
        val conditions = mutableListOf<String>()
        val params = mutableListOf<String>()
        val builder = ConditionBuilder()
        option["equal"]?.let {
            if (it.isNotEmpty() && it.size % 2 == 0) {
                builder.equalBuilder(it, conditions, params)
            }
        }
        option["objectContain"]?.let {
            if (it.isNotEmpty() && it.size % 3 == 0) {
                builder.objectContainBuilder(it, conditions, params)
            }
        }
        option["arrayContain"]?.let {
            if (it.isNotEmpty() && it.size % 2 == 0) {
                builder.arrayContainBuilder(it, conditions, params)
            }
        }
        option["like"]?.let {
            if (it.isNotEmpty() && it.size % 2 == 0) {
                builder.likeBuilder(it, conditions, params)
            }
        }
        option["objectLike"]?.let {
            if (it.isNotEmpty() && it.size % 3 == 0) {
                builder.objectLikeBuilder(it, conditions, params)
            }
        }
        option["in"]?.let {
            if (it.isNotEmpty() && it.size > 1) {
                builder.inBuilder(it, conditions, params)
            }
        }
        option["lesser"]?.let {
            if (it.isNotEmpty() && it.size % 2 == 0) {
                builder.lesserBuilder(it, conditions, params)
            }
        }
        option["greater"]?.let {
            if (it.isNotEmpty() && it.size % 2 == 0) {
                builder.greaterBuilder(it, conditions, params)
            }
        }
        if (conditions.size > 0) {
            q += conditions.joinToString(" and ", " where ")
        }
        q += " order by id desc limit $skip, $take"
        logger.debug(q)
        val result = jdbcTemplate.queryForList(q, *params.toTypedArray())
        return result
    }

}
