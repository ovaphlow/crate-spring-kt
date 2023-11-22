package ovaphlow.cdtlab.crate.events

import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/crate-api/events"])
class EventsController {
    @Autowired
    lateinit var eventsRepository: EventsRepository

    @RequestMapping(path = [""], method = [RequestMethod.GET])
    fun filter(
        @RequestParam(name = "option", defaultValue = "") option: String,
        @RequestParam(name = "skip", defaultValue = "0") skip: Long,
        @RequestParam(name = "take", defaultValue = "10") take: Int,
        request: HttpServletRequest
    ): ResponseEntity<Any> {
        if (option == "") {
            val relationId: Long = request.getParameter("relationId")?.toLongOrNull() ?: 0
            val referenceId: Long = request.getParameter("referenceId")?.toLongOrNull() ?: 0
            val tags = request.getParameter("tags")?.split(",") ?: listOf<String>()
            val detail = request.getParameter("detail")?.split(",") ?: listOf<Any>()
            val detailMap = mutableMapOf<String, Any>()
            detail.forEachIndexed { index, value ->
                if (index % 2 == 0) {
                    detailMap[value.toString()] = detail[index + 1]
                }
            }
            val timeRange = request.getParameter("timeRange")?.split(",") ?: listOf<String>()
            val result = eventsRepository.filter(skip, take, relationId, referenceId, tags, detailMap, timeRange)
            return ResponseEntity.ok().body(result)
        }
        return ResponseEntity.status(406).body(mapOf("error" to "invalid option"))
    }
}
