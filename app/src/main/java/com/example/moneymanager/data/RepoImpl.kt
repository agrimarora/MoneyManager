package com.example.moneymanager.data

import android.util.Log
import com.example.moneymanager.common.ResultState
import com.example.moneymanager.common.USER_COLLECTION
import com.example.moneymanager.common.model.ExpenseModel
import com.example.moneymanager.common.model.GoalModel
import com.example.moneymanager.common.model.IncomeModel
import com.example.moneymanager.common.model.UserData
import com.example.moneymanager.common.model.UserDataParent
import com.example.moneymanager.domain.Repo.Repo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Date
import javax.inject.Inject

class RepoImpl @Inject constructor(
    var firebaseAuth: FirebaseAuth, var firebaseFirestore: FirebaseFirestore,
) : Repo {


    override fun registeruserwithemailandpassword(userdata: UserData): Flow<ResultState<String>> =
        callbackFlow {

            trySend(ResultState.Loading)

            userdata.email?.let { email ->
                userdata.password?.let { password ->
                    if (email.isBlank() || password.isBlank()) {
                        trySend(ResultState.error("Email and password cannot be empty"))
                        return@callbackFlow
                    }
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {

                        if (it.isSuccessful) {
                            firebaseFirestore.collection(USER_COLLECTION)
                                .document(it.result.user?.uid.toString()).set(userdata)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        trySend(ResultState.Succes("User Registered Successfully"))

                                    } else {
                                        if (it.exception != null) {
                                            trySend(ResultState.error(it.exception!!.localizedMessage.toString()))
                                        }

                                    }
                                }


                        } else {

                            Log.d("error", "${it.exception!!.localizedMessage} ")
                            if (it.exception != null) trySend(ResultState.error(it.exception!!.localizedMessage.toString()))
                        }


                    }
                }
            }
            awaitClose { }


        }

    override fun loginuserwithemailandpassword(userdata: UserData): Flow<ResultState<String>> =
        callbackFlow {
            trySend(ResultState.Loading)
            userdata.email?.let { email ->
                userdata.password?.let { password ->
                    if (email.isBlank() || password.isBlank()) {
                        trySend(ResultState.error("Email and password cannot be empty"))
                        return@callbackFlow
                    }
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful) {
                            trySend(ResultState.Succes("User Logged In Successfully"))
                        } else {
                            Log.d("error", "${it.exception!!.localizedMessage} ")
                            if (it.exception != null) trySend(ResultState.error(it.exception!!.localizedMessage.toString()))

                        }
                    }
                }
            }
            awaitClose { }

        }



    override fun getuserbyUID(UID: String): Flow<ResultState<UserDataParent>> = callbackFlow {
        trySend(ResultState.Loading)

        firebaseFirestore.collection(USER_COLLECTION).document(UID).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val data = it.result.toObject(UserData::class.java)!!
                val UserData = UserDataParent(it.result.id, data)
                if (data != null) {
                    trySend(ResultState.Succes(UserData))
                }

            } else {
                if (it.exception != null)
                    trySend(ResultState.error(it.exception!!.localizedMessage.toString()))
                else {
                    if (it.exception != null) {
                        trySend(ResultState.error(it.exception!!.localizedMessage.toString()))
                    }
                }
            }

        }
        awaitClose { }


    }

    override fun addIncomeToUser(userData: UserData): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        val currentUser = firebaseAuth.currentUser?.uid


        val userDocRef =
            firebaseFirestore.collection(USER_COLLECTION).document(currentUser.toString())

        userDocRef.update("income", userData.income)
            .addOnSuccessListener {
                trySend(ResultState.Succes("Income updated successfully"))
            }
            .addOnFailureListener { e ->
                trySend(ResultState.error("Failed to update income: ${e.localizedMessage}"))
            }

        awaitClose { close() }
    }

    override fun updateUserData(userDataParent: UserDataParent): Flow<ResultState<String>> =
        callbackFlow {
            trySend(ResultState.Loading)
            val user = firebaseAuth.currentUser

            if (user != null && userDataParent.Userdata != null && userDataParent.Userdata.email != null) {
                user.updateEmail(userDataParent.Userdata.email!!)
                    .addOnCompleteListener { emailUpdateTask ->
                        if (emailUpdateTask.isSuccessful) {
                            val uid = user.uid
                            firebaseFirestore.collection(USER_COLLECTION).document(uid)
                                .set(userDataParent.Userdata!!)
                                .addOnCompleteListener { firestoreTask ->
                                    if (firestoreTask.isSuccessful) {
                                        trySend(ResultState.Succes("User Data Updated Successfully"))
                                    } else {
                                        trySend(
                                            ResultState.error(
                                                firestoreTask.exception?.localizedMessage
                                                    ?: "Unknown error updating Firestore"
                                            )
                                        )
                                    }
                                }
                        } else {
                            trySend(
                                ResultState.error(
                                    emailUpdateTask.exception?.localizedMessage
                                        ?: "Failed to update email"
                                )
                            )
                        }
                    }
            } else {
                trySend(ResultState.error("User is null or invalid user data"))
            }

            awaitClose {} // Important to close the flow
        }


    override fun getallexpenditure(): Flow<ResultState<List<ExpenseModel>>> = callbackFlow {
        trySend(ResultState.Loading)
        val user = firebaseAuth.currentUser
        val cartRef = firebaseFirestore.collection("${USER_COLLECTION}/${user?.uid}/Expenses")
        cartRef.get().addOnSuccessListener { querySnapShot ->
            try {
                val ExpenseList = querySnapShot.documents.mapNotNull { document ->
                    document.toObject(ExpenseModel::class.java)
                }
                Log.d("Expenses", "getallexpenditure: ${ExpenseList}")
                trySend(ResultState.Succes(ExpenseList))


            } catch (e: Exception) {

                trySend(ResultState.error(e.localizedMessage.toString()))

            }


        }
            .addOnFailureListener {
                trySend(ResultState.error(it.localizedMessage.toString()))
            }
        awaitClose { }


    }

    override fun getExpenditureByDateRange(
        startDate: Date,
        endDate: Date,
    ): Flow<ResultState<List<ExpenseModel>>> = callbackFlow {
        trySend(ResultState.Loading)

        val user = firebaseAuth.currentUser
        val cartRef = firebaseFirestore
            .collection("${USER_COLLECTION}/${user?.uid}/Expenses")
            .whereGreaterThanOrEqualTo("date", startDate)
            .whereLessThanOrEqualTo("date", endDate)

        cartRef.get()
            .addOnSuccessListener { querySnapshot ->
                try {
                    val expenseList = querySnapshot.documents.mapNotNull { document ->
                        document.toObject(ExpenseModel::class.java)
                    }
                    Log.d("Expenses", "getExpenditureByDateRange: $expenseList")
                    trySend(ResultState.Succes(expenseList))
                } catch (e: Exception) {
                    trySend(ResultState.error(e.localizedMessage ?: "Unknown error"))
                }
            }
            .addOnFailureListener {
                trySend(ResultState.error(it.localizedMessage ?: "Unknown error"))
            }

        awaitClose { }
    }
    override fun AddExpense(expensedata: ExpenseModel): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        val user = firebaseAuth.currentUser
        if (user == null) {
            trySend(ResultState.error("User not authenticated"))
            close()
            return@callbackFlow
        }

        val cartRef = firebaseFirestore.collection("${USER_COLLECTION}/${user.uid}/Expenses")
        val newDocRef = cartRef.document() // generates a new unique document reference
        val expenseWithId = expensedata.copy(id = newDocRef.id)
        newDocRef.set(expenseWithId).addOnSuccessListener { document ->
            trySend(ResultState.Succes("Expenses Added Successfully"))


        }.addOnFailureListener {
            trySend(ResultState.error(it.localizedMessage.toString()))
        }
        awaitClose { }


    }

    override fun setGoals(Goal: GoalModel): Flow<ResultState<String>> =callbackFlow{
        val user=firebaseAuth.currentUser
        val goalRef=firebaseFirestore.collection("${USER_COLLECTION}/${user?.uid}/Goals")
        if (user==null){
            trySend(ResultState.error("User not authenticated"))
            close()
            return@callbackFlow

        }
        val newDocRef = goalRef.document() // generates a new unique document reference
        val goalWithId = Goal.copy(id = newDocRef.id)
        newDocRef.set(goalWithId).addOnSuccessListener { document ->
            trySend(ResultState.Succes("Goal Added Successfully"))


        }.addOnFailureListener {
            trySend(ResultState.error(it.localizedMessage.toString()))
        }
        awaitClose { }





    }
    override fun deleteGoal(goalId: String): Flow<ResultState<String>> = callbackFlow {
        val user = firebaseAuth.currentUser
        if (user == null) {
            trySend(ResultState.error("User not authenticated"))
            close()
            return@callbackFlow
        }

        val goalRef = firebaseFirestore
            .collection("${USER_COLLECTION}/${user.uid}/Goals")
            .document(goalId)

        goalRef.delete()
            .addOnSuccessListener {
                trySend(ResultState.Succes("Goal deleted successfully"))
            }
            .addOnFailureListener { exception ->
                trySend(ResultState.error(exception.localizedMessage ?: "Failed to delete goal"))
            }

        awaitClose { close() }
    }

    override fun editGoal(goal: GoalModel): Flow<ResultState<String>> = callbackFlow {
        val user = firebaseAuth.currentUser
        if (user == null) {
            trySend(ResultState.error("User not authenticated"))
            close()
            return@callbackFlow
        }

        if (goal.id.isNullOrEmpty()) {
            trySend(ResultState.error("Invalid goal ID"))
            close()
            return@callbackFlow
        }

        val goalRef = firebaseFirestore
            .collection("${USER_COLLECTION}/${user.uid}/Goals")
            .document(goal.id)


        val updatedData = mapOf(
            "target" to goal.target,
            "targetAmount" to goal.targetAmount,
            "amount" to goal.amount,
            "progres" to goal.progres,
            "Date" to goal.Date,
            "achieved" to goal.achieved
        )

        goalRef.update(updatedData)
            .addOnSuccessListener {
                trySend(ResultState.Succes("Goal updated successfully"))
            }
            .addOnFailureListener { exception ->
                trySend(ResultState.error(exception.localizedMessage ?: "Failed to update goal"))
            }

        awaitClose { close() }
    }


    override fun getGoals(): Flow<ResultState<List<GoalModel>>> =callbackFlow {
        trySend(ResultState.Loading)
        val user=firebaseAuth.currentUser
        val cartRef = firebaseFirestore
            .collection("${USER_COLLECTION}/${user?.uid}/Goals")
        cartRef.get().addOnSuccessListener {
            try {
                val goalList = it.documents.mapNotNull { document ->
                    document.toObject(GoalModel::class.java)
                }
                trySend(ResultState.Succes(goalList))

            }
            catch (e: Exception){
                trySend(ResultState.error(e.localizedMessage.toString()))
            }
        }
        awaitClose { }


    }


}