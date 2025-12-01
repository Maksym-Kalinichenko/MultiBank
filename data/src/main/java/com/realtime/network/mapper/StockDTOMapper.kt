package com.realtime.network.mapper

import com.realtime.common.mapper.Mapper
import com.realtime.data.model.StockDataModel
import com.realtime.network.model.StockDTO
import javax.inject.Inject

class StockDTOMapper @Inject constructor() : Mapper<StockDTO, StockDataModel> {
    override fun from(i: StockDTO): StockDataModel {
        return StockDataModel(
            name = i.name,
            price = i.price
        )
    }

    override fun to(o: StockDataModel): StockDTO {
        return StockDTO(
            name = o.name,
            price = o.price
        )
    }
}