package com.amulet.cavinist.persistence.repository.wine

import com.amulet.cavinist.persistence.*
import com.amulet.cavinist.persistence.data.wine.*
import com.amulet.cavinist.persistence.repository.CrudRepository
import com.amulet.cavinist.persistence.repository.wine.crud.CrudWineryRepository
import org.intellij.lang.annotations.Language
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.*
import java.util.UUID

@Repository
class WineryRepository(private val databaseClient: DatabaseClient, override val crudRepository: CrudWineryRepository) :
    CrudRepository<WineryEntity>() {

    fun findForUser(id: UUID, userId: UUID): Mono<WineryEntity> = crudRepository.findByIdAndUserId(id, userId)

    @Language("SQL")
    private fun query(userId: UUID) =
        """SELECT winery.id as winery_id, winery.version as winery_version, winery.name as winery_name, 
            winery_region.id as winery_region_id, winery_region.name as winery_region_name, 
            winery_region.country as winery_region_country, winery_region.user_id as winery_region_user_id, 
            winery_region.version as winery_region_version
            FROM wineries as winery 
                INNER JOIN regions as winery_region ON winery.region_id = winery_region.id
            WHERE winery.user_id='$userId'""".trimIndent()

    fun findAllForUser(userId: UUID): Flux<WineryWithDependencies> {
        return databaseClient.sql(query(userId))
            .map { row ->
                WineryWithDependencies(
                    row.getUUID("winery_id"),
                    row.getInt("winery_version"),
                    row.getString("winery_name"),
                    RegionEntity(
                        row.getUUID("winery_region_id"),
                        row.getInt("winery_region_version"),
                        row.getString("winery_region_name"),
                        row.getString("winery_region_country"),
                        row.getUUID("winery_region_user_id")))
            }
            .all()
    }

}