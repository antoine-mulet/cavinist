package com.amulet.cavinist.persistence.wine

import com.amulet.cavinist.common.WordSpecIT
import com.amulet.cavinist.persistence.data.wine.*
import com.amulet.cavinist.persistence.repository.OutdatedVersionException
import com.amulet.cavinist.persistence.repository.wine.WineRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.*
import io.kotest.matchers.collections.*
import io.kotest.matchers.throwable.haveMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import java.util.UUID

class WineRepositoryIT : WordSpecIT() {

    @Autowired
    lateinit var repository: WineRepository

    init {

        "findForUser" should {
            "return the correct wine when it exists" {
                repository.findForUser(dataSet.petrusWine.ID, dataSet.userOneId).block() shouldBe dataSet.petrusWine
            }

            "return null when the wine doesn't exists for the user" {
                repository.findForUser(dataSet.petrusWine.ID, dataSet.userTwoId).block() shouldBe null
            }

            "return null when the wine doesn't exists at all" {
                repository.findForUser(UUID.randomUUID(), dataSet.userTwoId).block() shouldBe null
            }
        }

        "findAllForUser" should {
            "return the correct wines" {
                val res: List<WineWithDependencies> =
                    repository.findAllForUser(dataSet.userOneId).collectList().block()!!
                res should haveSize(3)
                val petrusWine = WineWithDependencies(
                    dataSet.petrusWine.ID,
                    dataSet.petrusWine.version(),
                    dataSet.petrusWine.name,
                    dataSet.petrusWine.type,
                    dataSet.petrusWineryWithDependencies,
                    dataSet.pomerolRegion)
                val laFleurPetrusWine = WineWithDependencies(
                    dataSet.laFleurPetrusWine.ID,
                    dataSet.laFleurPetrusWine.version(),
                    dataSet.laFleurPetrusWine.name,
                    dataSet.laFleurPetrusWine.type,
                    dataSet.petrusWineryWithDependencies,
                    dataSet.pomerolRegion)
                val lesCalcairesWine = WineWithDependencies(
                    dataSet.lesCalcairesWine.ID,
                    dataSet.lesCalcairesWine.version(),
                    dataSet.lesCalcairesWine.name,
                    dataSet.lesCalcairesWine.type,
                    dataSet.cazeneuveWineryWithDependencies,
                    dataSet.picSaintLoupRegion)
                res should containAll(petrusWine, laFleurPetrusWine, lesCalcairesWine)
            }
        }

        "save" should {
            "save a new wine" {
                val newWine = WineEntity(
                    UUID.randomUUID(),
                    null,
                    "Cazeneuve",
                    WineType.ROSE,
                    dataSet.cazeneuveWinery.ID,
                    dataSet.languedocRegion.ID,
                    dataSet.userOneId)
                val res = repository.save(newWine).block()!!
                repository.findById(res.ID).block() shouldBe newWine.copy(version = 0)
            }

            "update an existing wine" {
                val updatedWine = dataSet.lesCalcairesWine.copy(regionId = dataSet.languedocRegion.ID)
                val versionBefore = updatedWine.version()
                val res = repository.save(updatedWine).block()!!
                res shouldBe updatedWine.copy(version = versionBefore + 1)
            }

            "fail properly when trying to save an already existing wine" {
                val exception = shouldThrow<DataIntegrityViolationException> {
                    repository.save(dataSet.laFleurPetrusWine.copy(ID = UUID.randomUUID(), version = null)).block()!!
                }
                exception should haveMessage("Wine with name '${dataSet.laFleurPetrusWine.name}', type '${dataSet.laFleurPetrusWine.type}', winery id '${dataSet.laFleurPetrusWine.wineryId}' and region id '${dataSet.laFleurPetrusWine.regionId}' already exists.")
            }

            "fail to update an outdated version" {
                val exception = shouldThrow<OutdatedVersionException> {
                    repository.save(dataSet.lesCalcairesWine.copy(name = "Les Calcairess", version = 0)).block()!!
                }
                exception should haveMessage("Failed to update Wine with id '${dataSet.lesCalcairesWine.id}' because version is outdated.")
            }
        }
    }

}