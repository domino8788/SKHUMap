const functions = require('firebase-functions')
const admin = require('firebase-admin')
admin.initializeApp()
const db = admin.firestore()

exports.createUser = functions.auth.user().onCreate(async (user) => {
    const userInfo= user.email.split(/[@.]/g)
    const id = userInfo[0]
    const userName = userInfo[1]
    
    await db.doc(`users/${id}`).set({
        uid : user.uid,
        name : userName
    })

    console.info(`${id} ${userName} 회원가입 \nuid : ${user.uid}`)
})