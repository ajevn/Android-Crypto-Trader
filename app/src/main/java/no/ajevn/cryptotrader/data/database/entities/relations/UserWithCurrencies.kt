package no.ajevn.cryptotrader.data.database.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import no.ajevn.cryptotrader.data.database.entities.User
import no.ajevn.cryptotrader.data.database.entities.UserCurrencies
import no.ajevn.cryptotrader.data.database.entities.UserTransactions


data class UserWithCurrencies(
    @Embedded val user: User,
    @Relation(
            parentColumn = "user_id",
            entityColumn = "user_id"
    )
    val userCurrencies: List<UserCurrencies>
)

data class UserWithTransactions(
        @Embedded val user: User,
        @Relation(
                parentColumn = "user_id",
                entityColumn = "user_id"
        )
        val userTransactions: List<UserTransactions>
)