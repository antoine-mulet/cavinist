package com.amulet.cavinist.web.mutation

import com.amulet.cavinist.persistence.data.wine.WineType
import com.amulet.cavinist.web.common.WordSpecWebIT
import com.amulet.cavinist.web.graphql.ErrorCode

class WineMutationIT : WordSpecWebIT() {

    private val createWineMutation = "createWine"

    init {

        createWineMutation should {
            "create a new wine along with its winery and regions" {
                testQuery(
                    createWineMutation,
                    """mutation {
                          $createWineMutation(
                            wineInput: {
                              name: "A l'ombre du Figuier"
                              type: RED
                              wineryInput: {
                                name: "Mas de la Seranne"
                                regionInput: { name: "Languedoc", country: "France" }
                              }
                              regionInput: { name: "Terrasses du Larzac", country: "France" }
                            }
                          ) { id, version, name, type, winery {
                                id, version, name, region {
                                    id, name, country, version
                                }
                             }, region {
                                id, name, country, version
                             }
                          }
                        }""".trimIndent())
                    .verifyData(
                        "version" to 0,
                        "name" to "A l'ombre du Figuier",
                        "type" to WineType.RED.toString(),
                        "winery.version" to 0,
                        "winery.name" to "Mas de la Seranne",
                        "winery.region.version" to 0,
                        "winery.region.name" to "Languedoc",
                        "winery.region.country" to "France",
                        "region.version" to 0,
                        "region.name" to "Terrasses du Larzac",
                        "region.country" to "France")
            }

            "create a new wine from an existing winery and region" {
                testQuery(
                    createWineMutation,
                    """mutation {
                          $createWineMutation(
                            wineInput: {
                              name: "Le Sang du Calvaire"
                              type: RED
                              wineryInput: { id: "${dataSet.cazeneuveWinery.id}" }
                              regionInput: { id: "${dataSet.picSaintLoupRegion.id}" }
                            }
                          ) { id, version, name, type, winery {
                                id, version, name, region {
                                    id, name, country, version
                                }
                             }, region {
                                id, name, country, version
                             }
                          }
                        }""".trimIndent())
                    .verifyData(
                        "version" to 0,
                        "name" to "Le Sang du Calvaire",
                        "type" to WineType.RED.toString(),
                        "winery.version" to dataSet.cazeneuveWinery.version(),
                        "winery.id" to dataSet.cazeneuveWinery.id.toString(),
                        "winery.name" to dataSet.cazeneuveWinery.name,
                        "winery.region.version" to dataSet.languedocRegion.version(),
                        "winery.region.name" to dataSet.languedocRegion.name,
                        "winery.region.country" to dataSet.languedocRegion.country,
                        "region.version" to dataSet.picSaintLoupRegion.version(),
                        "region.id" to dataSet.picSaintLoupRegion.id.toString(),
                        "region.name" to dataSet.picSaintLoupRegion.name,
                        "region.country" to dataSet.picSaintLoupRegion.country)
            }

            "fail to create the very same wine a second time" {
                testQuery(
                    createWineMutation,
                    """mutation {
                          $createWineMutation(
                            wineInput: {
                              name: "Le Sang du Calvaire"
                              type: RED
                              wineryInput: { id: "${dataSet.cazeneuveWinery.id}" }
                              regionInput: { id: "${dataSet.picSaintLoupRegion.id}" }
                            }
                          ) { id, version, name, type, winery {
                                id, version, name, region {
                                    id, name, country, version
                                }
                             }, region {
                                id, name, country, version
                             }
                          }
                        }""".trimIndent())
                    .verifyError(
                        ErrorCode.OBJECT_ALREADY_EXISTS,
                        "Wine with name 'Le Sang du Calvaire', type 'RED', winery id '${dataSet.cazeneuveWinery.id}' and region id '${dataSet.picSaintLoupRegion.id}' already exists.")
            }

            "fail when referencing a non existing winery" {
                testQuery(
                    createWineMutation,
                    """mutation {
                          $createWineMutation(
                            wineInput: {
                              name: "Fake One"
                              type: RED
                              wineryInput: { id: "39240e9f-ae09-4e95-9fd0-a712035c8ad8" }
                              regionInput: { name: "Should not be saved", country: "fake" }
                            }
                          ) { id, version, name, type, winery {
                                id, version, name, region {
                                    id, name, country, version
                                }
                             }, region {
                                id, name, country, version
                             }
                          }
                        }""".trimIndent())
                    .verifyError(
                        ErrorCode.OBJECT_NOT_FOUND,
                        "Winery with id '39240e9f-ae09-4e95-9fd0-a712035c8ad8' not found.")
            }
        }
    }
}