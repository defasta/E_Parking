package apps.eduraya.e_parking.data.db

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "my_data_store")

class UserPreferences @Inject constructor(@ApplicationContext context: Context) {

    private val appContext = context.applicationContext

    val accessToken: Flow<String?>
        get() = appContext.dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN]
        }

    val insurancePrice: Flow<String?>
        get() = appContext.dataStore.data.map { preferences ->
            preferences[INSURANCE_PRICE]
        }

    val insuranceDetail: Flow<String?>
        get() = appContext.dataStore.data.map { preferences ->
            preferences[INSURANCE_DETAIL]
        }

    val isInsurance: Flow<String?>
        get() = appContext.dataStore.data.map { preferences ->
            preferences[IS_INSURANCE]
        }

    val isInsuranceRequest: Flow<String?>
        get() = appContext.dataStore.data.map { preferences ->
            preferences[IS_INSURANCE_REQUEST]
        }

    val isCheckin: Flow<String?>
        get() = appContext.dataStore.data.map { preferences ->
            preferences[IS_CHECKIN]
        }

    val idLastParking: Flow<String?>
        get() = appContext.dataStore.data.map { preferences ->
            preferences[ID_LAST_PARKING]
        }

    val refreshToken: Flow<String?>
        get() = appContext.dataStore.data.map { preferences ->
            preferences[REFRESH_TOKEN]
        }

    val depositToken: Flow<String?>
        get() = appContext.dataStore.data.map { preferences ->
            preferences[DEPOSIT_TOKEN]
        }

    suspend fun saveAccessTokens(accessToken: String) {
        appContext.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = accessToken
        }
    }

    suspend fun saveInsurancePriceInfo(insurancePrice: String){
        appContext.dataStore.edit { preferences ->
            preferences[INSURANCE_PRICE] = insurancePrice
        }
    }

    suspend fun saveInsuranceDetailInfo(insuranceDetail: String){
        appContext.dataStore.edit { preferences ->
            preferences[INSURANCE_DETAIL] = insuranceDetail
        }
    }

    suspend fun saveIdLastParking(id: String){
        appContext.dataStore.edit { preferences ->
            preferences[ID_LAST_PARKING] = id
        }
    }

    suspend fun isInsurance(isInsurance: String){
        appContext.dataStore.edit { preferences ->
            preferences[IS_INSURANCE] = isInsurance
        }
    }

    suspend fun isInsuranceRequest(isInsurance: String){
        appContext.dataStore.edit { preferences ->
            preferences[IS_INSURANCE_REQUEST] = isInsurance
        }
    }
    suspend fun isCheckin(isCheckin: String){
        appContext.dataStore.edit { preferences ->
            preferences[IS_CHECKIN] = isCheckin
        }
    }

    suspend fun clear() {
        appContext.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("key_access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("key_refresh_token")
        private val DEPOSIT_TOKEN = stringPreferencesKey("key_deposit_token")
        private val INSURANCE_PRICE = stringPreferencesKey("key_insurance_price")
        private val INSURANCE_DETAIL = stringPreferencesKey("key_insurance_detail")
        private val IS_INSURANCE = stringPreferencesKey("key_is_insurance")
        private val IS_INSURANCE_REQUEST = stringPreferencesKey("key_is_insurance_request")
        private val IS_CHECKIN = stringPreferencesKey("key_is_checkin")
        private val ID_LAST_PARKING = stringPreferencesKey("key_id_last_parking")

    }

}