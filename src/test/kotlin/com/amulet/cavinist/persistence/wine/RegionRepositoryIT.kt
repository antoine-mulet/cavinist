package com.amulet.cavinist.persistence.wine

import com.amulet.cavinist.common.WordSpecIT
import com.amulet.cavinist.persistence.data.wine.RegionEntity
import com.amulet.cavinist.persistence.repository.OutdatedVersionException
import com.amulet.cavinist.persistence.repository.wine.RegionRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.containAll
import io.kotest.matchers.collections.haveSize
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.haveMessage
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import java.util.UUID

class RegionRepositoryIT : WordSpecIT() {

    @Autowired
    private lateinit var repository: RegionRepository

    init {

        "findForUser" should {
            "return the correct region when it exists" {
                runBlocking {
                    repository.findForUser(dataSet.languedocRegion.ID, dataSet.userOneId)
                } shouldBe dataSet.languedocRegion
            }

            "return null when the region doesn't exists for the user" {
                runBlocking {
                    repository.findForUser(dataSet.languedocRegion.ID, dataSet.userTwoId)
                } shouldBe null
            }

            "return null when the region doesn't exists at all" {
                runBlocking {
                    repository.findForUser(UUID.randomUUID(), dataSet.userTwoId)
                } shouldBe null
            }
        }

        "findAllForUser" should {
            "return the correct regions" {
                val res = runBlocking {
                    repository.findAllForUser(dataSet.userOneId)
                }
                res should haveSize(3)
                res should containAll(dataSet.pomerolRegion, dataSet.picSaintLoupRegion, dataSet.languedocRegion)
            }
        }

        "save" should {
            "save a new region" {
                val newRegion = RegionEntity(UUID.randomUUID(), null, "New region", "New country", dataSet.userOneId)
                val res = runBlocking { repository.save(newRegion) }
                runBlocking { repository.findById(res.ID) } shouldBe newRegion.copy(version = 0)
            }

            "update an existing region" {
                val updatedRegion = dataSet.picSaintLoupRegion.copy(name = "Pic-St-Loup")
                val versionBefore = updatedRegion.version()
                runBlocking { repository.save(updatedRegion) } shouldBe updatedRegion.copy(version = versionBefore + 1)
            }

            "fail properly when trying to save an already existing region" {
                val exception = shouldThrow<DataIntegrityViolationException> {
                    runBlocking {
                        repository.save(dataSet.languedocRegion.copy(ID = UUID.randomUUID(), version = null))
                    }
                }
                exception should haveMessage("Region with name '${dataSet.languedocRegion.name}', country '${dataSet.languedocRegion.country}' already exists.")
            }

            "fail to update an outdated version" {
                val exception = shouldThrow<OutdatedVersionException> {
                    runBlocking {
                        repository.save(dataSet.picSaintLoupRegion.copy(name = "Pik-Saint-Loup", version = 0))
                    }
                }
                exception should haveMessage("Failed to update Region with id '${dataSet.picSaintLoupRegion.id}' because version is outdated.")
            }
        }

    }

}