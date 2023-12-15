package ovaphlow.cdtlab.crate.utility

class ConditionBuilder {

    fun equalBuilder(equal: List<String>, conditions: MutableList<String>, params: MutableList<String>) {
        for (i in equal.indices step 2) {
            conditions += "${equal[i]} = ?"
            params += equal[i + 1]
        }
    }

    fun objectContainBuilder(
        objectContain: List<String>,
        conditions: MutableList<String>,
        params: MutableList<String>
    ) {
        for (i in objectContain.indices step 3) {
            conditions += "json_contains(${objectContain[i]}, json_object('${objectContain[i + 1]}', ?))"
            params += objectContain[i + 2]
        }
    }

    fun arrayContainBuilder(arrayContain: List<String>, conditions: MutableList<String>, params: MutableList<String>) {
        for (i in arrayContain.indices step 2) {
            conditions += "json_contains(${arrayContain[i]}, json_array(?))"
            params += arrayContain[i + 1]
        }
    }

    fun likeBuilder(like: List<String>, conditions: MutableList<String>, params: MutableList<String>) {
        for (i in like.indices step 2) {
            conditions += "position(? in ${like[i]})"
            params += like[i + 1]
        }
    }

    fun objectLikeBuilder(objectLike: List<String>, conditions: MutableList<String>, params: MutableList<String>) {
        for (i in objectLike.indices step 3) {
            conditions += "position(? in ${objectLike[i]}->>'$.${objectLike[i + 1]}')"
            params += objectLike[i + 2]
        }
    }

    fun inBuilder(inList: List<String>, conditions: MutableList<String>, params: MutableList<String>) {
        val c: List<String> = List(inList.size - 1) { "?" }
        conditions += "${inList[0]} in (${c.joinToString(", ")})"
        params += inList.subList(1, inList.size)
    }

    fun lesserBuilder(lesser: List<String>, conditions: MutableList<String>, params: MutableList<String>) {
        for (i in lesser.indices step 2) {
            conditions += "${lesser[i]} <= ?"
            params += lesser[i + 1]
        }
    }

    fun greaterBuilder(greater: List<String>, conditions: MutableList<String>, params: MutableList<String>) {
        for (i in greater.indices step 2) {
            conditions += "${greater[i]} >= ?"
            params += greater[i + 1]
        }
    }

}
