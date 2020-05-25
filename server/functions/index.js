const functions = require('firebase-functions')
const admin = require('firebase-admin')
admin.initializeApp()
const db = admin.firestore()

exports.createUser = functions.auth.user().onCreate((user) => {
    const userInfo= user.email.split(/[@.]/g)
    const id = userInfo[0]
    const userName = userInfo[1]
    
    db.doc(`users/${id}`).set({
        uid : user.uid,
        name : userName
    })

    console.info(`${id} ${userName} 회원가입 \nuid : ${user.uid}`)
})

const createKeywords = (keyword) => {
    const arr = []
    let cur = ''
    for(let i=0;i<keyword.length;i++) {
        for(let letter of keyword.slice(i).split('')){
            cur += letter
            arr.push(cur)
        }
        cur=""
    }
    return arr
}

const generateKeywords = (id, names) => {
    let keywordsArray = createKeywords(id)
    if(names){
        names.forEach((name) => {
            createKeywords(name).forEach((word) => {
                if(keywordsArray.indexOf(word) === -1)
                    keywordsArray.push(word)
            }) 
        })
    }
    return keywordsArray
}

exports.allKeywordsGenerate =  functions.https.onRequest(async (req, res) => {
    let log = "execute allKeywordsGenerate\n"

    const departments = await db.collection('facilities').listDocuments()
    const departmentsPromise = []
    departments.forEach((department) => {
        departmentsPromise.push(department.listCollections())
    })

    const allFloors = await Promise.all(departmentsPromise)
    const floorsPromise = []
    allFloors.forEach(floors => {
        floors.forEach(floor => {
            floorsPromise.push(floor.get());
        })
    })

    const allFacilities = await Promise.all(floorsPromise);
    allFacilities.forEach((facilities) => {
        facilities.forEach((facility) => {
            const floor = facility.ref.parent
            const department = floor.parent
            log += `${department.id}_${floor.id}_${facility.id}\n`
            
            let data= facility.data()
            let id = facility.id
            let names = data["name"]
    
            db.doc(`search/${department.id}_${floor.id}_${facility.id}`).set({
                keyword: [id].concat(names),
                keywords : generateKeywords(id, names)
            })
        })
    })
    log+="complete allKeywordsGenerate\n"
    res.send(302, `<pre>${log}</pre>`)
})

exports.facilityChange = functions.firestore
.document('facilities/{department}/{floor}/{facility}')
.onWrite((change, context) => {
    const target = db.doc(`search/${context.params.department}_${context.params.floor}_${context.params.facility}`)
    const value = change.after
    if(value.exists) {
        const data = value.data()
        console.log(value.id)
        target.set({
            keyword : [value.id].concat(data["name"]),
            keywords: generateKeywords(value.id, data["name"])
         })
    }
    else{
        target.delete()
    }
})