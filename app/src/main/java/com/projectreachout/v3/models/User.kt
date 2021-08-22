package com.projectreachout.v3.models

import com.google.firebase.firestore.DocumentSnapshot
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

class User {
    companion object {
        fun fromDoc(doc: DocumentSnapshot): User {
            val userEntity: UserEntity? = doc.toObject(UserEntity::class.java)
            return Gson().fromJson(Gson().toJson(userEntity), User::class.java)
        }
    }

    val userId: String? = null
    val username: String? = null
    val profileImageUrl: String? = null
    val displayName: String? = null
    val email: String? = null
    val phoneNumber: String? = null
    val userType: String? = null
    val bio: String? = null

    private class UserEntity() {
        @SerializedName("userId")
        val user_id: String? = null
        val username: String? = null

        @SerializedName("profileImageUrl")
        val profile_image_url: String? = null

        @SerializedName("displayName")
        val display_name: String? = null
        val email: String? = null

        @SerializedName("phoneNumber")
        val phone_number: String? = null

        @SerializedName("userType")
        val user_type: String? = null
        val bio: String? = null
    }
}