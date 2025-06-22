package com.example.moneymanager.domain

import com.example.moneymanager.data.RepoImpl
import com.example.moneymanager.domain.Repo.Repo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {
    @Provides
    fun providesrepo(firebaseAuth: FirebaseAuth,firebaseFirestore: FirebaseFirestore): Repo {
        return RepoImpl(firebaseAuth,firebaseFirestore)


    }
}