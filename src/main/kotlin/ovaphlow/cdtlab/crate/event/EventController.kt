package ovaphlow.cdtlab.crate.event

import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ovaphlow.cdtlab.crate.utility.SharedRepository

@RestController
@RequestMapping(path = ["/crate-api/events"])
class EventController {

    @Autowired
    lateinit var sharedRepository: SharedRepository

    @RequestMapping(path = [""], method = [RequestMethod.GET])
    fun filter(
        @RequestParam(name = "option", defaultValue = "") option: String,
        @RequestParam(name = "skip", defaultValue = "0") skip: Long,
        @RequestParam(name = "take", defaultValue = "10") take: Int,
        request: HttpServletRequest
    ): ResponseEntity<Any> {
        if (option == "default") {
            val equal = request.getParameter("equal")?.split(",") ?: listOf()
            val objectContain = request.getParameter("object-contain")?.split(",") ?: listOf()
            val arrayContain = request.getParameter("array-contain")?.split(",") ?: listOf()
            val like = request.getParameter("like")?.split(",") ?: listOf()
            val objectLike = request.getParameter("object-like")?.split(",") ?: listOf()
            val inList = request.getParameter("in")?.split(",") ?: listOf()
            val lesser = request.getParameter("lesser")?.split(",") ?: listOf()
            val greater = request.getParameter("greater")?.split(",") ?: listOf()
            val result = sharedRepository.retrieve(
                "event",
                skip,
                take,
                mapOf(
                    "equal" to equal,
                    "objectContain" to objectContain,
                    "arrayContain" to arrayContain,
                    "like" to like,
                    "objectLike" to objectLike,
                    "in" to inList,
                    "lesser" to lesser,
                    "greater" to greater,
                ),
            )
            val response: List<MutableMap<String, Any?>> = result.map {
                mutableMapOf(
                    "id" to it["id"],
                    "relationId" to it["relation_id"],
                    "referenceId" to it["reference_id"],
                    "tags" to it["tags"],
                    "detail" to it["detail"],
                    "time" to it["time"],
                    "_id" to it["id"],
                    "_relationId" to it["relation_id"],
                    "_referenceId" to it["reference_id"],
                )
            }
            return ResponseEntity.ok().body(response)
        }
        return ResponseEntity.status(406).body(mapOf("message" to "invalid option"))
    }
}
