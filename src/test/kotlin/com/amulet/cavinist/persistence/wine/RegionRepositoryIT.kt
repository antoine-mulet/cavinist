package com.amulet.cavinist.persistence.wine

import com.amulet.cavinist.common.WordSpecIT
import com.amulet.cavinist.persistence.data.wine.RegionEntity
import com.amulet.cavinist.persistence.repository.OutdatedVersionException
import com.amulet.cavinist.persistence.repository.wine.RegionRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.*
import io.kotest.matchers.collections.*
import io.kotest.matchers.throwable.haveMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import java.util.UUID

class RegionRepositoryIT : WordSpecIT() {

    @Autowired
    private lateinit var repository: RegionRepository

    init {

        "findById" should {
            "return the correct region when it exists" {
                repository.findById(dataSet.languedocRegion.ID).block() shouldBe dataSet.languedocRegion
            }

            "return null when the region doesn't exists" {
                repository.findById(UUID.randomUUID()).block() shouldBe null
            }
        }

        "findAll" should {
            "return the correct regions" {
                val res = repository.findAll().collectList().block()!!
                res should haveSize(3)
                res should containAll(dataSet.pomerolRegion, dataSet.picSaintLoupRegion, dataSet.languedocRegion)
            }
        }

        "save" should {
            "save a new region" {
                val newRegion = RegionEntity(UUID.randomUUID(), null, "New region", "New country")
                val res = repository.save(newRegion).block()!!
                repository.findById(res.ID).block() shouldBe newRegion.copy(version = 0)
            }

            "update an existing region" {
                val updatedRegion = dataSet.picSaintLoupRegion.copy(name = "Pic-St-Loup")
                val versionBefore = updatedRegion.version()
                val res = repository.save(updatedRegion).block()!!
                res shouldBe updatedRegion.copy(version = versionBefore + 1)
            }

            "fail properly when trying to save an already existing region" {
                val exception = shouldThrow<DataIntegrityViolationException> {
                    repository.save(dataSet.languedocRegion.copy(ID = UUID.randomUUID(), version = null)).block()!!
                }
                exception should haveMessage("Region with name '${dataSet.languedocRegion.name}', country '${dataSet.languedocRegion.country}' already exists.")
            }

            "fail to update an outdated version" {
                val exception = shouldThrow<OutdatedVersionException> {
                    repository.save(dataSet.picSaintLoupRegion.copy(name = "Pik-Saint-Loup", version = 0)).block()!!
                }
                exception should haveMessage("Failed to update Region with id '${dataSet.picSaintLoupRegion.id}' because version is outdated.")
            }
        }

    }

}