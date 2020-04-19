package com.amulet.cavinist.persistence

import com.amulet.cavinist.common.WordSpecIT
import com.amulet.cavinist.persistence.data.RegionEntity
import com.amulet.cavinist.persistence.repository.*
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
                repository.findById(dataSet.languedocRegion.id).block() shouldBe dataSet.languedocRegion
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
                val res = repository.save(RegionEntity(UUID.randomUUID(), null, "New region", "New country")).block()!!
                repository.findById(res.id).block() shouldBe res.copy(version = 0)
            }

            "update an existing region" {
                val res = repository.save(dataSet.picSaintLoupRegion.copy(name = "Pic-St-Loup")).block()!!
                res.version() == 1
                res shouldBe dataSet.picSaintLoupRegion.copy(name = "Pic-St-Loup", version = 1)
            }

            "fail properly when trying to save an already existing region" {
                val exception = shouldThrow<DataIntegrityViolationException> {
                    repository.save(dataSet.languedocRegion.copy(id = UUID.randomUUID(), version = null)).block()!!
                }
                exception should haveMessage("Region with name '${dataSet.languedocRegion.name}', country '${dataSet.languedocRegion.country}' already exists.")
            }

            "fail to update an outdated version" {
                val exception = shouldThrow<OutdatedVersionException> {
                    repository.save(dataSet.picSaintLoupRegion.copy(name = "Pik-Saint-Loup", version = 0)).block()!!
                }
                exception should haveMessage("Failed to update region with id '${dataSet.picSaintLoupRegion.id}' because version is outdated.")
            }
        }

    }

}