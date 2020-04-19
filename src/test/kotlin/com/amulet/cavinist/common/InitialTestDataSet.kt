package com.amulet.cavinist.common

import com.amulet.cavinist.persistence.data.*
import com.amulet.cavinist.persistence.repository.*
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class InitialTestDataSet(
    wineRepository: WineRepository,
    wineryRepository: WineryRepository,
    regionRepository: RegionRepository) {

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

    val cazeneuveWinery: WineryEntity by lazy {
        wineryRepository.findById(UUID.fromString("39240e9f-ae09-4e95-9fd0-a712035c8ad7"))
            .block()!!
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