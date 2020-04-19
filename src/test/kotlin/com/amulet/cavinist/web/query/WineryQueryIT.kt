package com.amulet.cavinist.web.query

import com.amulet.cavinist.web.common.WordSpecWebIT
import java.util.UUID

class WineryQueryIT : WordSpecWebIT() {

    private val getWineryQuery = "getWinery"
    private val listWineriesQuery = "listWineries"

    init {

        getWineryQuery should {

            val query =
                { id: UUID -> """query { $getWineryQuery(id: "$id") {id, version, name, region { id, version, name, country } } }""" }

            "return the correct winery" {
                testQuery(getWineryQuery, query(dataSet.petrusWinery.id)).verifyData(
                    "id" to dataSet.petrusWinery.id.toString(),
                    "version" to dataSet.petrusWinery.version(),
                    "name" to dataSet.petrusWinery.name,
                    "region.id" to dataSet.pomerolRegion.id.toString(),
                    "region.version" to dataSet.pomerolRegion.version(),
                    "region.name" to dataSet.pomerolRegion.name,
                    "region.country" to dataSet.pomerolRegion.country)
            }

            "return null if the winery does not exist" {
                testQuery(getWineryQuery, query(UUID.randomUUID())).verifyEmpty()
            }
        }

        listWineriesQuery should {

            val query = "query { $listWineriesQuery {id, version, name, region { id, version, name, country } } }"

            "return the list of all the wineries" {
                testQuery(listWineriesQuery, query).verifyArraySize(2)
            }
        }
    }
}