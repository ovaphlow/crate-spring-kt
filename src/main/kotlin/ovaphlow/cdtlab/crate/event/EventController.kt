package ovaphlow.cdtlab.crate.event

import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/crate-api/events"])
class EventController {
    @Autowired
    lateinit var eventRepository: EventRepository

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
            val result = eventRepository.defaultFilter(
                skip,
                take,
                equal,
                objectContain,
                arrayContain,
                like,
                objectLike,
                inList,
                lesser,
                greater
            )
            val response: MutableList<Map<String, Any>> = mutableListOf()
            for (i in result.indices) {
                response += mapOf(
                    "id" to result[i].id,
                    "relationId" to result[i].relationId,
                    "referenceId" to result[i].referenceId,
                    "tags" to result[i].tags,
                    "detail" to result[i].detail,
                    "time" to result[i].time,
                    "_id" to result[i].id.toString(),
                    "_relationId" to result[i].relationId.toString(),
                    "_referenceId" to result[i].referenceId.toString(),
                )
            }
            return ResponseEntity.ok().body(response)
        }
        return ResponseEntity.status(406).body(mapOf("error" to "invalid option"))
    }
}
