package com.amulet.cavinist.web.query

import com.amulet.cavinist.web.common.WordSpecWebIT
import java.util.UUID

class RegionQueryIT : WordSpecWebIT() {

    private val getRegionQuery = "getRegion"
    private val listRegionsQuery = "listRegions"

    init {

        getRegionQuery should {

            val query = { id: UUID -> """query { $getRegionQuery(id: "$id") {id, version, name, country } }""" }

            "return the correct region" {
                testQuery(getRegionQuery, query(dataSet.languedocRegion.ID)).verifyData(
                    "id" to dataSet.languedocRegion.id.toString(),
                    "version" to dataSet.languedocRegion.version(),
                    "name" to dataSet.languedocRegion.name,
                    "country" to dataSet.languedocRegion.country)
            }

            "return null if the region does not exist" {
                testQuery(getRegionQuery, query(UUID.randomUUID())).verifyEmpty()
            }
        }

        listRegionsQuery should {

            val query = "query { $listRegionsQuery { id, version, name, country } }"

            "return the list of all the wineries" {
                testQuery(listRegionsQuery, query).verifyArraySize(3)
            }
        }
    }
}