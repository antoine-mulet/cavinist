package com.amulet.cavinist.persistence.repository.wine

import com.amulet.cavinist.persistence.*
import com.amulet.cavinist.persistence.data.wine.*
import com.amulet.cavinist.persistence.repository.CrudRepository
import com.amulet.cavinist.persistence.repository.wine.crud.CrudWineryRepository
import org.intellij.lang.annotations.Language
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
class WineryRepository(private val databaseClient: DatabaseClient, override val crudRepository: CrudWineryRepository) :
    CrudRepository<WineryEntity>() {

    @Language("SQL")
    private val query =
        """SELECT winery.id as winery_id, winery.version as winery_version, winery.name as winery_name, 
            winery_region.id as winery_region_id, winery_region.name as winery_region_name, 
            winery_region.country as winery_region_country, winery_region.version as winery_region_version
            FROM wineries as winery 
                INNER JOIN regions as winery_region ON winery.region_id = winery_region.id""".trimIndent()

    fun findAllWithDependencies(): Flux<WineryWithDependencies> {
        return databaseClient.execute(query)
            .map { row ->
                WineryWithDependencies(
                    row.getUUID("winery_id"),
                    row.getInt("winery_version"),
                    row.getString("winery_name"),
                    RegionEntity(
                        row.getUUID("winery_region_id"),
                        row.getInt("winery_region_version"),
                        row.getString("winery_region_name"),
                        row.getString("winery_region_country")))
            }
            .all()
    }

}