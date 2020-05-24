package com.amulet.cavinist.persistence.repository.wine

import com.amulet.cavinist.persistence.*
import com.amulet.cavinist.persistence.data.wine.*
import com.amulet.cavinist.persistence.repository.CrudRepository
import com.amulet.cavinist.persistence.repository.wine.crud.CrudWineRepository
import org.intellij.lang.annotations.Language
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
class WineRepository(private val databaseClient: DatabaseClient, override val crudRepository: CrudWineRepository) :
    CrudRepository<WineEntity>() {

    @Language("SQL")
    private val query =
        """SELECT wine.id as wine_id, wine.version as wine_version, wine.name as wine_name, wine.type as wine_type, 
            winery.id as winery_id, winery.version as winery_version, winery.name as winery_name, 
            winery_region.id as winery_region_id, winery_region.name as winery_region_name, 
            winery_region.country as winery_region_country, winery_region.version as winery_region_version, 
            wine_region.id as wine_region_id, wine_region.name as wine_region_name, 
            wine_region.country as wine_region_country, winery_region.version as wine_region_version 
            FROM wines as wine 
                INNER JOIN wineries as winery 
                    INNER JOIN regions as winery_region ON winery.region_id = winery_region.id 
                ON wine.winery_id = winery.id 
                INNER JOIN regions as wine_region ON wine.region_id = wine_region.id""".trimIndent()

    fun findAllWithDependencies(): Flux<WineWithDependencies> {
        return databaseClient.execute(query)
            .map { row ->
                WineWithDependencies(
                    row.getUUID("wine_id"),
                    row.getInt("wine_version"),
                    row.getString("wine_name"),
                    row.getWineType("wine_type"),
                    WineryWithDependencies(
                        row.getUUID("winery_id"),
                        row.getInt("winery_version"),
                        row.getString("winery_name"),
                        RegionEntity(
                            row.getUUID("winery_region_id"),
                            row.getInt("winery_region_version"),
                            row.getString("winery_region_name"),
                            row.getString("winery_region_country"))),
                    RegionEntity(
                        row.getUUID("wine_region_id"),
                        row.getInt("wine_region_version"),
                        row.getString("wine_region_name"),
                        row.getString("wine_region_country")))
            }
            .all()
    }
}