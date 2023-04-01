package com.amulet.cavinist.persistence.wine

import com.amulet.cavinist.common.WordSpecIT
import com.amulet.cavinist.persistence.data.wine.WineryEntity
import com.amulet.cavinist.persistence.repository.OutdatedVersionException
import com.amulet.cavinist.persistence.repository.wine.WineryRepository
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

class WineryRepositoryIT : WordSpecIT() {

    @Autowired
    private lateinit var repository: WineryRepository

    init {

        "findForUser" should {
            "return the correct winery when it exists" {
                runBlocking {
                    repository.findForUser(dataSet.cazeneuveWinery.ID, dataSet.userOneId)
                } shouldBe dataSet.cazeneuveWinery
            }

            "return null when the winery doesn't exists for the user" {
                runBlocking {
                    repository.findForUser(dataSet.cazeneuveWinery.ID, dataSet.userTwoId)
                } shouldBe null
            }

            "return null when the winery doesn't exists at all" {
                runBlocking {
                    repository.findForUser(UUID.randomUUID(), dataSet.userTwoId)
                } shouldBe null
            }
        }

        "findAllForUser" should {
            "return the correct wineries" {
                val res = runBlocking { repository.findAllForUser(dataSet.userOneId) }
                res should haveSize(2)
                res should containAll(dataSet.petrusWineryWithDependencies, dataSet.cazeneuveWineryWithDependencies)
            }
        }

        "save" should {
            "save a new winery" {
                val newWinery =
                    WineryEntity(UUID.randomUUID(), null, "New Winery", dataSet.languedocRegion.ID, dataSet.userOneId)
                val res = runBlocking { repository.save(newWinery) }
                runBlocking { repository.findById(res.ID) } shouldBe newWinery.copy(version = 0)
            }

            "update an existing region" {
                val updatedWinery = dataSet.cazeneuveWinery.copy(name = "Cazeneuve Winery")
                val versionBefore = updatedWinery.version()
                runBlocking { repository.save(updatedWinery) } shouldBe updatedWinery.copy(version = versionBefore + 1)
            }

            "fail properly when trying to save an already existing region" {
                val exception = shouldThrow<DataIntegrityViolationException> {
                    runBlocking {
                        repository.save(dataSet.petrusWinery.copy(ID = UUID.randomUUID(), version = null))
                    }
                }
                exception should haveMessage("Winery with name '${dataSet.petrusWinery.name}', region id '${dataSet.petrusWinery.regionId}' already exists.")
            }

            "fail to update an outdated version" {
                val exception = shouldThrow<OutdatedVersionException> {
                    runBlocking {
                        repository.save(dataSet.cazeneuveWinery.copy(name = "Domaine de Cazeneuve", version = 0))
                    }
                }
                exception should haveMessage("Failed to update Winery with id '${dataSet.cazeneuveWinery.id}' because version is outdated.")
            }
        }
    }
}