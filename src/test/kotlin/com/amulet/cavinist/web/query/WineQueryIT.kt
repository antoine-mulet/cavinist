package com.amulet.cavinist.web.query

import com.amulet.cavinist.web.common.WordSpecWebIT
import java.util.UUID

class WineQueryIT : WordSpecWebIT() {

    private val getWineQuery = "getWine"
    private val listWinesQuery = "listWines"

    init {

        getWineQuery should {

            val query = { id: UUID ->
                """query { 
                    $getWineQuery(id: "$id") { id, version, name, winery {
                            id, version, name, region {
                                id, version, name, country
                            }
                        }, 
                        region {
                            id, version, name, country
                        }
                    }
                }""".trimIndent()
            }

            "return the correct wine" {
                testQuery(getWineQuery, query(dataSet.petrusWine.ID)).verifyData(
                    "id" to dataSet.petrusWine.id.toString(),
                    "version" to dataSet.petrusWine.version(),
                    "name" to dataSet.petrusWine.name,
                    "winery.id" to dataSet.petrusWinery.id.toString(),
                    "winery.version" to dataSet.petrusWinery.version(),
                    "winery.name" to dataSet.petrusWinery.name,
                    "winery.region.id" to dataSet.pomerolRegion.id.toString(),
                    "winery.region.version" to dataSet.pomerolRegion.version(),
                    "winery.region.name" to dataSet.pomerolRegion.name,
                    "winery.region.country" to dataSet.pomerolRegion.country,
                    "region.id" to dataSet.pomerolRegion.id.toString(),
                    "region.version" to dataSet.pomerolRegion.version(),
                    "region.name" to dataSet.pomerolRegion.name,
                    "region.country" to dataSet.pomerolRegion.country)
            }

            "return null if the wine does not exist" {
                testQuery(getWineQuery, query(UUID.randomUUID())).verifyEmpty()
            }
        }

        listWinesQuery should {

            val query = """query { 
                    $listWinesQuery { id, version, name, winery {
                            id, version, name, region {
                                id, version, name, country
                            }
                        }, 
                        region {
                            id, version, name, country
                        }
                    }
                }""".trimIndent()

            "return the list of all the wines" {
                testQuery(listWinesQuery, query).verifyArraySize(3)
            }
        }
    }
}