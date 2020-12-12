package com.amulet.cavinist.common

import com.amulet.cavinist.persistence.data.user.UserEntity
import com.amulet.cavinist.persistence.data.wine.*
import com.amulet.cavinist.persistence.repository.user.UserRepository
import com.amulet.cavinist.persistence.repository.wine.*
import com.amulet.cavinist.security.JwtUtils
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class InitialTestDataSet(
    userRepository: UserRepository,
    wineRepository: WineRepository,
    wineryRepository: WineryRepository,
    regionRepository: RegionRepository,
    jwtUtils: JwtUtils) {

    val userOneId: UUID = UUID.fromString("eb7d367f-d0a6-4906-8584-43bc8b9f5cad")
    val userOne: UserEntity by lazy {
        userRepository.findById(userOneId).block()!!
    }
    val userOneJwt: String by lazy { jwtUtils.issueJwtForUser(userOneId) }

    val userTwoId: UUID = UUID.fromString("c9bff611-1206-4661-924d-5e86f551f430")
    val userTwo: UserEntity by lazy {
        userRepository.findById(userTwoId).block()!!
    }
    val userTwoJwt: String by lazy { jwtUtils.issueJwtForUser(userTwoId) }

    val pomerolRegion: RegionEntity by lazy {
        regionRepository.findById(UUID.fromString("2e744843-bf6a-4914-80fd-a802b5a952cb")).block()!!
    }

    val languedocRegion: RegionEntity by lazy {
        regionRepository.findById(UUID.fromString("66b2eb51-c6ce-49b8-9957-16a2d0b3dece")).block()!!
    }

    val picSaintLoupRegion: RegionEntity by lazy {
        regionRepository.findById(UUID.fromString("f56ba615-45e4-4e81-8b59-b1a6265142a8")).block()!!
    }

    val petrusWinery: WineryEntity by lazy {
        wineryRepository.findById(UUID.fromString("0d60a85e-0b90-4482-a14c-108aea2557aa"))
            .block()!!
    }

    val petrusWineryWithDependencies: WineryWithDependencies by lazy {
        WineryWithDependencies(petrusWinery.ID, petrusWinery.version(), petrusWinery.name, pomerolRegion)
    }

    val cazeneuveWinery: WineryEntity by lazy {
        wineryRepository.findById(UUID.fromString("39240e9f-ae09-4e95-9fd0-a712035c8ad7"))
            .block()!!
    }

    val cazeneuveWineryWithDependencies: WineryWithDependencies by lazy {
        WineryWithDependencies(cazeneuveWinery.ID, cazeneuveWinery.version(), cazeneuveWinery.name, languedocRegion)
    }

    val petrusWine: WineEntity by lazy {
        wineRepository.findById(UUID.fromString("9e4de779-d6a0-44bc-a531-20cdb97178d2")).block()!!
    }

    val laFleurPetrusWine: WineEntity by lazy {
        wineRepository.findById(UUID.fromString("66a45c1b-19af-4ab5-8747-1b0e2d79339d")).block()!!
    }

    val lesCalcairesWine: WineEntity by lazy {
        wineRepository.findById(UUID.fromString("bc8250bb-f7eb-4adc-925c-2af315cc4a55")).block()!!
    }
}