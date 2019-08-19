package proj2

// You MUST NOT change what you import.  If you add ANY additional
// imports it will break the autograder, and we will be Very Upset.

import (
	// You neet to add with
	// go get github.com/nweaver/cs161-p2/userlib
	"github.com/nweaver/cs161-p2/userlib"

	// Life is much easier with json:  You are
	// going to want to use this so you can easily
	// turn complex structures into strings etc...
	"encoding/json"

	// Likewise useful for debugging etc
	"encoding/hex"

	// UUIDs are generated right based on the crypto RNG
	// so lets make life easier and use those too...
	//
	// You need to add with "go get github.com/google/uuid"
	"github.com/google/uuid"

	// Useful for debug messages, or string manipulation for datastore keys
	"strings"

	// Want to import errors
	"errors"

	// optional
	_ "strconv"

	// if you are looking for fmt, we don't give you fmt, but you can use userlib.DebugMsg
	// see someUsefulThings() below
)

// This serves two purposes: It shows you some useful primitives and
// it suppresses warnings for items not being imported
func someUsefulThings() {
	// Creates a random UUID
	f := uuid.New()
	userlib.DebugMsg("UUID as string:%v", f.String())

	// Example of writing over a byte of f
	f[0] = 10
	userlib.DebugMsg("UUID as string:%v", f.String())

	// takes a sequence of bytes and renders as hex
	h := hex.EncodeToString([]byte("fubar"))
	userlib.DebugMsg("The hex: %v", h)

	// Marshals data into a JSON representation
	// Will actually work with go structures as well
	d, _ := json.Marshal(f)
	userlib.DebugMsg("The json data: %v", string(d))
	var g uuid.UUID
	json.Unmarshal(d, &g)
	userlib.DebugMsg("Unmashaled data %v", g.String())

	// This creates an error type
	userlib.DebugMsg("Creation of error %v", errors.New(strings.ToTitle("This is an error")))

	// And a random RSA key.  In this case, ignoring the error
	// return value
	//var pk userlib.PKEEncKey
  //      var sk userlib.PKEDecKey
	//pk, sk, _ = userlib.PKEKeyGen()
	//userlib.DebugMsg("Key is %v, %v", pk, sk)
}

// Helper function: Takes the first 16 bytes and
// converts it into the UUID type
func bytesToUUID(data []byte) (ret uuid.UUID) {
	for x := range ret {
		ret[x] = data[x]
	}
	return
}
// Helper function: Generate keys to encrypt and decrypt userdata sturcts.
// Returns error if unsuccessful.
func userKeys(username string, password string) (keyOne []byte, keyTwo []byte, keyThree []byte, err error) {
	var errTwo error
	err = nil
	keyOne = userlib.Argon2Key([]byte(password), []byte(username), uint32(16))
	HMACByte, errTwo := userlib.HMACEval(keyOne, []byte(password))
	if errTwo != nil {
		return nil, nil, nil, errTwo
	}
	keyTwo = HMACByte[:16]
	keyThree = HMACByte[16:32]
	return keyOne, keyTwo, keyThree, err
}

// Helper function: Check if username/password is valid.
func checkUser(username string, password string) (check bool) {
	//Implement
	return username != "" && password != "" && len(username) < 20 && len(password) < 20
}

// Equal ttgyhujikijuhygtfrdftgyhujmnhbgvfcdrfvtghjkells whether a and b contain the same elements.
// A nil argument is equivalent to an empty slice.
// Copied from Internet
func testEq(a, b []byte) bool {

    // If one is nil, the other must also be nil.
    if (a == nil) != (b == nil) {
        return false
    }

    if len(a) != len(b) {
        return false
    }

    for i := range a {
        if a[i] != b[i] {
            return false
        }
    }

    return true
}

// The structure definition for a user record
type User struct {
	Username string
	Password string
	//Useruuid uuid.UUID
	RSAPrivatePKey userlib.DSSignKey
	EncryptPrivateKey userlib.PKEDecKey
	KeysForFiles map[string][2][]byte
	UuidForFiles map[string][2][]byte
	// You can add other fields here if you want...
	// Note for JSON to marshal/unmarshal, the fields need to
	// be public (start with a capital letter)
}



// This creates a user.  It will only be called once for a user
// (unless the keystore and datastore are cleared during testing purposes)

// It should store a copy of the userdata, suitably encrypted, in the
// datastore and should store the user's public key in the keystore.

// The datastore may corrupt or completely erase the stored
// information, but nobody outside should be able to get at the stored
// User data: the name used in the datastore should not be guessable
// without also knowing the password and username.

// You are not allowed to use any global storage other than the
// keystore and the datastore functions in the userlib library.

// You can assume the user has a STRONG password
func InitUser(username string, password string) (userdataptr *User, err error) {
	check := checkUser(username, password)
	if check == false {
		errEnter := errors.New(strings.ToTitle("Username or password is invalid."))
		return nil, errEnter
	}
	if _, alreadyExist := userlib.KeystoreGet(username + "PKE"); alreadyExist {
		errRepeat := errors.New(strings.ToTitle("Username has already be taken."))
		return nil, errRepeat
	}
	var userdata User
	userdataptr = &userdata
	userdata.Username = username
	userdata.Password = password

	// Set RSA keys.
	RSAprivate, RSApublic, RSAerr := userlib.DSKeyGen()
	if RSAerr != nil{
		return nil, RSAerr
	}
	userdata.RSAPrivatePKey = RSAprivate
	Setkeyerr := userlib.KeystoreSet(username + "RSA", RSApublic)
	if Setkeyerr != nil{
		return nil, Setkeyerr
	}
	// Set public encryption keys.
	PKEpub, PKEpri, PKEerr := userlib.PKEKeyGen()
	if PKEerr!= nil{
		return nil, PKEerr
	}
	Setkeyerr = userlib.KeystoreSet(username + "PKE", PKEpub)
	if Setkeyerr != nil{
		return nil, Setkeyerr
	}
	userdata.EncryptPrivateKey = PKEpri
	// Create a map of files mapping to each file's id, keys, and latest data pointer.
	userdata.KeysForFiles = make(map[string][2][]byte)
	userdata.UuidForFiles = make(map[string][2][]byte)
	// Converting userdata from a struct to a byte slice.
	d, _ := json.Marshal(userdata)
	userSlice := []byte(d)
	// Generate keys for encrypting the userdata.
	keyOne, keyTwo, keyThree, keyErr := userKeys(username, password)
	if keyErr != nil{
		return nil, keyErr
	}
	// Encrypt the userslice
	IV := userlib.RandomBytes(16)
	userSliceEnc := userlib.SymEnc(keyOne, IV, userSlice)
	//calculate HMAC of encrypted user struct
	HMACofUser, errSig := userlib.HMACEval(keyThree, userSliceEnc)
	if errSig != nil{
		return nil, errSig
	}
	//calculate the id to store user struct
	userID, errID := userlib.HMACEval(keyTwo, []byte(username))
	if errID != nil || len(userID) < 16{
		return nil, errID
	}
	//store the userstruct on Datastore
	stored := append(HMACofUser, userSliceEnc...)
	userlib.DatastoreSet(bytesToUUID(userID), stored)
	return &userdata, nil
}

func Dummy(x int, y int)(z int){
	 if x < y {
		 return x + y
	 }
	 if x > y {
		 return x - y
	 }
	 return x * y
}

func Dummy2(x int)(z int){
	 if x == 1 {
		 return 1
	 }
	 if x == 2 {
		 return 2
	 }
	 if x == 3{
		 return 3
	 }
	 if x == 4{
		 return 4
	 }
	 if x == 5{
		 return 5
	 }
	 if x == 6{
		 return 6
	 }
	 if x == 7{
		 return 7
	 }
	 if x == 8{
		 return 8
	 }
	 if x == 9{
		 return 9
	 }
	 if x == 10{
		 return 10
	 }
	 if x == 11{
		 return 11
	 }
	 if x == 12{
		 return 12
	 }
	 if x == 13{
		 return 13
	 }
	 if x == 14{
		 return 14
	 }
	 if x == 15{
		 return 15
	 }
	 return x * x
}


// This fetches the user information from the Datastore.  It should
// fail with an error if the user/password is invalid, or if the user
// data was corrupted, or if the user can't be found.
func GetUser(username string, password string) (userdataptr *User, err error) {
	var userdata User
	userdataptr = &userdata
	// Check if user/password is invalid.
	check := checkUser(username, password)
	if check == false {
		errEnter := errors.New(strings.ToTitle("Username or password is invalid."))
		return nil, errEnter
	}
	// Generate keys for decrypting the userdata.
	keyOne, keyTwo, keyThree, keyErr := userKeys(username, password)
	if keyErr != nil{
		return nil, keyErr
	}
	// Get the data stored under the UUID we computed from the keys.
	userID, errID := userlib.HMACEval(keyTwo, []byte(username))
	if errID != nil{
		return nil, errID
	}
	// Check if RSAkey can be found.
	_, RSAexist := userlib.KeystoreGet(username + "RSA")
	if RSAexist == false {
		RSAerr := errors.New(strings.ToTitle("RSAkey can't be found."))
		return nil, RSAerr
	}
	//check if Public EncryptionKey can be found
	_, PKEexist := userlib.KeystoreGet(username + "PKE")
	if PKEexist == false {
		PKEerr := errors.New(strings.ToTitle("PKEkey can't be found."))
		return nil, PKEerr
	}
	//check if User can be found
	if len(userID) < 16 {
		CorruptedUser := errors.New(strings.ToTitle("UserName is somehow corrupted."))
		return nil, CorruptedUser
	}
	userSliceEnc, Userexist := userlib.DatastoreGet(bytesToUUID(userID))
	if Userexist == false || len(userSliceEnc) < 64 {
		Usererr := errors.New(strings.ToTitle("User name can't be found."))
		return nil, Usererr
	}
	// Verify the signature to determine whether data was corrupted or not.
	HMACget := userSliceEnc[:64]
	HMACcompute, errSig := userlib.HMACEval(keyThree, userSliceEnc[64:])
	if errSig != nil{
		return nil, errSig
	}
	if userlib.HMACEqual(HMACget, HMACcompute) == false {
		errVerify := errors.New(strings.ToTitle("Userdata has been corrupted."))
		return nil, errVerify
	}
	// If everything is ok, decrypt the user struct into user data.
	userSlice := userlib.SymDec(keyOne, userSliceEnc[64:])
	json.Unmarshal(userSlice, &userdata)
	return userdataptr, nil
}


type FileNode struct {
	FileContent []byte
  Next []byte
}
// This stores a file in the datastore.
//
// The name of the file should NOT be revealed to the datastore!
func (userdata *User) StoreFile(filename string, data []byte) {
	// Check if filename is valid
    if filename == "" || len(filename) > 20 {
		return
	}
  //Generate two symmertic keys for the file
	EncryptionKey := userlib.RandomBytes(16)
	MacKey := userlib.RandomBytes(16)
	randomByteSlice := userlib.RandomBytes(16)
	randomUUID := bytesToUUID(randomByteSlice)
	//file := FileNode.New()
	var file FileNode
	file.FileContent = data
	//update the latestPointer with the next UUID of file
	file.Next = userlib.RandomBytes(16)
	//encrypt and store the value here
	encryptTail := userlib.SymEnc(EncryptionKey, userlib.RandomBytes(16), file.Next)
	HMACval,HMACerr := userlib.HMACEval(MacKey, encryptTail)
	if HMACerr != nil {
		return
	}
	randomPlace := userlib.RandomBytes(16)
	place := bytesToUUID(randomPlace)
	userlib.DatastoreSet(place, append(HMACval, encryptTail...))
	//assign two maps in user struct
	var keys [2][]byte
  keys[0] = EncryptionKey
  keys[1] = MacKey
	var ids  [2][]byte
	ids[0] = randomByteSlice
  ids[1] = randomPlace
	userdata.KeysForFiles[filename] = keys
	userdata.UuidForFiles[filename] = ids
	//storing the file
	fileSlice, _ := json.Marshal(file)
	fileSlice = userlib.SymEnc(EncryptionKey, userlib.RandomBytes(16), fileSlice)
	HMACval,HMACerr = userlib.HMACEval(MacKey, fileSlice)
	if HMACerr != nil {
		return
	}
	userlib.DatastoreSet(randomUUID, append(HMACval, fileSlice...))


  //UPDATING the user
	// Converting userdata from a struct to a byte slice.
	d, _ := json.Marshal(userdata)
	userSlice := []byte(d)
	// Generate keys for encrypting the userdata.
	keyOne, keyTwo, keyThree, keyErr := userKeys(userdata.Username, userdata.Password)
	if keyErr != nil{
		return
	}
	// Encrypt the userslice
	IV := userlib.RandomBytes(16)
	userSliceEnc := userlib.SymEnc(keyOne, IV, userSlice)
	//calculate HMAC of encrypted user struct
	HMACofUser, errSig := userlib.HMACEval(keyThree, userSliceEnc)
	if errSig != nil{
		return
	}
	//calculate the id to store user struct
	userID, errID := userlib.HMACEval(keyTwo, []byte(userdata.Username))
	if errID != nil || len(userID) < 16{
		return
	}
	//store the userstruct on Datastore
	stored := append(HMACofUser, userSliceEnc...)
	userlib.DatastoreSet(bytesToUUID(userID), stored)
	return
}

// This adds on to an existing file.
//
// Append should be efficient, you shouldn't rewrite or reencrypt the
// existing file, but only whatever additional information and
// metadata you need.

func (userdata *User) AppendFile(filename string, data []byte) (err error) {
	//Check invalid string
	if filename == "" || len(filename) > 20 {
		errStr := errors.New(strings.ToTitle("File name is not good."))
		return errStr
	}
	//First, we get the uuid to appendfile
	placeSlice := userdata.UuidForFiles[filename][1]
	if len(placeSlice) < 16 {
		DataCorrupted := errors.New(strings.ToTitle("Invalid identification data for file."))
		return DataCorrupted
	}
	place := bytesToUUID(placeSlice)
	insert, exist := userlib.DatastoreGet(place)
	if exist == false || len(insert) < 64 {
		Notfound := errors.New(strings.ToTitle("LatestPointer can't be found."))
		return Notfound
	}
	//Then we decrypt this value
	EncryptionKey := userdata.KeysForFiles[filename][0]
	MacKey := userdata.KeysForFiles[filename][1]
	//Verfiy that MAC is the same
	HMACget := insert[:64]
	HMACcompute, HMACerr := userlib.HMACEval(MacKey, insert[64:])
	if HMACerr != nil{
		return HMACerr
	}
	if userlib.HMACEqual(HMACget, HMACcompute) == false {
		errCorrupt := errors.New(strings.ToTitle("Pointer Value corrupted!!!!! >_<"))
		return errCorrupt
	}
	//Then decrypt the insert value
	insert = userlib.SymDec(EncryptionKey, insert[64:])
	if len(insert) < 16 {
		DataCorrupted := errors.New(strings.ToTitle("Invalid identification data for file."))
		return DataCorrupted
	}

	insertUUID := bytesToUUID(insert)
	//get the key for Encryption
	EncryptionKey = userdata.KeysForFiles[filename][0]
	MacKey = userdata.KeysForFiles[filename][1]
	//Now we insert into this UUID the new file
	var file FileNode
	file.FileContent = data
	file.Next = userlib.RandomBytes(16)
	//encrypt the file
	fileSlice, _ := json.Marshal(file)
	fileSlice = userlib.SymEnc(EncryptionKey, userlib.RandomBytes(16), fileSlice)
	HMACval,HMACerr := userlib.HMACEval(MacKey, fileSlice)
	if HMACerr != nil {
		return
	}
	userlib.DatastoreSet(insertUUID, append(HMACval, fileSlice...))

	//encrypt file.next
	encryptTail := userlib.SymEnc(EncryptionKey, userlib.RandomBytes(16), file.Next)
	HMACval,HMACerr = userlib.HMACEval(MacKey, encryptTail)
	if HMACerr != nil {
		return
	}
	userlib.DatastoreSet(place, append(HMACval,encryptTail...))
	return
}

// This loads a file from the Datastore.
//
// It should give an error if the file is corrupted in any way.
func (userdata *User) LoadFile(filename string) (data []byte, err error) {
	if filename == "" || len(filename) > 20 {
		errStr := errors.New(strings.ToTitle("File name is not good."))
		return []byte{}, errStr
	}
	errKey := errors.New(strings.ToTitle("Key does not exist. >_<"))
	keys, ok := userdata.KeysForFiles[filename]
	if ok == false{
		return []byte{}, errKey
	}
	encryptKey, macKey := keys[0], keys[1]
	ids, ok := userdata.UuidForFiles[filename]
	if ok == false{
		return []byte{}, errKey
	}
	headSlice, tailSlice := ids[0], ids[1]
	if len(headSlice) < 16 || len(tailSlice) < 16{
		DataCorrupted := errors.New(strings.ToTitle("Invalid identification data for file."))
		return nil, DataCorrupted
	}
	head := bytesToUUID(headSlice)
	tail := bytesToUUID(tailSlice)
	// Define errors.
	err404 := errors.New(strings.ToTitle("File does not exist. >_<"))
	errCorrupt := errors.New(strings.ToTitle("File corrupted!!!!! >_<"))
	//get the file from Datastore
	MacAndEncrypt, exist := userlib.DatastoreGet(head)
	if exist == false || len(MacAndEncrypt) < 64 {
		return nil, err404
	}
	//Verfiy that MAC is the same
	HMACget := MacAndEncrypt[:64]
	HMACcompute, HMACerr:= userlib.HMACEval(macKey, MacAndEncrypt[64:])
	if HMACerr != nil{
		return nil, HMACerr
	}
	if userlib.HMACEqual(HMACget, HMACcompute) == false {
		return nil, errCorrupt
	}
	// Begin to decrypt file content.
	fileSlice := userlib.SymDec(encryptKey, MacAndEncrypt[64:])
	var fileStruct FileNode
	json.Unmarshal(fileSlice, &fileStruct)
	next := fileStruct.Next
	data = fileStruct.FileContent
	//gets and decrypt the tail
	tailVal, exist := userlib.DatastoreGet(tail)
	if exist == false || len(tailVal) < 64{
		return nil, err404
	}
	HMACget = tailVal[:64]
	HMACcompute, HMACerr = userlib.HMACEval(macKey, tailVal[64:])
	if HMACerr != nil{
		return nil, HMACerr
	}
	if userlib.HMACEqual(HMACget, HMACcompute) == false {
		return nil, errCorrupt
	}
	//Then decrypt the tail value
	tailVal = userlib.SymDec(encryptKey, tailVal[64:])
	//begin to iterate through the linkedlist
	for testEq(next, tailVal) == false {
		if len(next) < 16 {
			DataCorrupted := errors.New(strings.ToTitle("Invalid identification data for file."))
			return nil, DataCorrupted
		}
		nextasUUID := bytesToUUID(next)
		MacAndEncrypt, exist = userlib.DatastoreGet(nextasUUID)
		if exist == false || len(MacAndEncrypt) < 64{
			return nil, err404
		}
		//Verfiy that MAC is the same
		HMACget = MacAndEncrypt[:64]
		HMACcompute, HMACerr = userlib.HMACEval(macKey, MacAndEncrypt[64:])
		if HMACerr != nil{
			return nil, HMACerr
		}
		if userlib.HMACEqual(HMACget, HMACcompute) == false {
			return nil, errCorrupt
		}
		//Decrypt the current package.
		fileSlice = userlib.SymDec(encryptKey, MacAndEncrypt[64:])
		json.Unmarshal(fileSlice, &fileStruct)
		next = fileStruct.Next
		data = append(data, fileStruct.FileContent...)
	}
	return data, nil
}

// You may want to define what you actually want to pass as a
// sharingRecord to serialized/deserialize in the data store.
type sharingRecord struct {
   PointerToUUid []byte
	 EncryptionKey []byte
	 MacKey []byte
}

// This creates a sharing record, which is a key pointing to something
// in the datastore to share with the recipient.

// This enables the recipient to access the encrypted file as well
// for reading/appending.

// Note that neither the recipient NOR the datastore should gain any
// information about what the sender calls the file.  Only the
// recipient can access the sharing record, and only the recipient
// should be able to know the sender.

func (userdata *User) ShareFile(filename string, recipient string) (
	magic_string string, err error) {
  	EncryptKey := userdata.KeysForFiles[filename][0]
	HMACkey := userdata.KeysForFiles[filename][1]
	HeadID := userdata.UuidForFiles[filename][0]
	TailID := userdata.UuidForFiles[filename][1]
	//Encrypt HEADID and TailID appended togetehr
	IDCombined := append(HeadID, TailID...)
	IDSlice := userlib.SymEnc(EncryptKey, userlib.RandomBytes(16), IDCombined)
	HMACval,HMACerr := userlib.HMACEval(HMACkey, IDSlice)
	if HMACerr != nil {
		return "", HMACerr
	}
	Pointer := userlib.RandomBytes(16)
	uuidPointer := bytesToUUID(Pointer)
	userlib.DatastoreSet(uuidPointer, append(HMACval, IDSlice...))
  	var toShare sharingRecord
	toShare.EncryptionKey = EncryptKey
	toShare.MacKey = HMACkey
	toShare.PointerToUUid = Pointer
	//get the two keys from KeyStore
	publicEncryptionKey, exist := userlib.KeystoreGet(recipient + "PKE")
	if exist == false {
		err404 := errors.New(strings.ToTitle("PKEKey does not exist. >_<"))
		return "", err404
	}
	RSASignKey := userdata.RSAPrivatePKey
	//encrypt and sign SharingRecord
	sharingSlice, _ := json.Marshal(toShare)
  sharingSliceEnc, PKEerr := userlib.PKEEnc(publicEncryptionKey,sharingSlice)
	if PKEerr != nil{
		return "", PKEerr
	}
  signature, RSAerr := userlib.DSSign(RSASignKey, sharingSliceEnc)
	if RSAerr != nil{
		return "", RSAerr
	}
	magic_slice := append(signature, sharingSliceEnc...)
	magic_string = string(magic_slice)
	return magic_string, nil
}

// Note recipient's filename can be different from the sender's filename.
// The recipient should not be able to discover the sender's view on
// what the filename even is!  However, the recipient must ensure that
// it is authentically from the sender.
func (userdata *User) ReceiveFile(filename string, sender string,
	magic_string string) error {
	//userlib.DebugMsg("Cannot verify0")
	magic_slice := []byte(magic_string)
	//userlib.DebugMsg("Cannot verify1")
	if len(magic_slice) < 256 {
		errTampered := errors.New(strings.ToTitle("The magic string sent here is obivsouly tampered!"))
		return errTampered
	}
	signature := magic_slice[:256]
	//userlib.DebugMsg("Cannot verify2")
	encryption := magic_slice[256:]
	//get Two keys
	//userlib.DebugMsg("Cannot verify0")
	RSAVerfiyKey, exist := userlib.KeystoreGet(sender + "RSA")
	if exist == false {
		err404 := errors.New(strings.ToTitle("RSAKey does not exist. >_<"))
		return err404
	}
	PKEDecKey := userdata.EncryptPrivateKey
	//verify signature
	RSAerr := userlib.DSVerify(RSAVerfiyKey, encryption,signature)
	if RSAerr != nil{
		return RSAerr
	}
	shareSlice, decryptErr := userlib.PKEDec(PKEDecKey, encryption)
	if decryptErr != nil{
		return decryptErr
	}
	var record *sharingRecord
	json.Unmarshal(shareSlice, &record)



  //Use the pointer to Get Head And Tail
	pointer := record.PointerToUUid
	if len(pointer) < 16 {
		Pointererr := errors.New(strings.ToTitle("Something wrong with the pointer"))
		return Pointererr
	}
  IDCombined, ok := userlib.DatastoreGet(bytesToUUID(pointer))
  if ok == false || len(IDCombined) < 64{
		 err404 := errors.New(strings.ToTitle("Can't Use pointer. >_<"))
		 return err404
	}
	//Verfiy that MAC is the same
	HMACget := IDCombined[:64]
	HMACcompute, HMACerr:= userlib.HMACEval(record.MacKey, IDCombined[64:])
	if HMACerr != nil{
		return HMACerr
	}
	if userlib.HMACEqual(HMACget, HMACcompute) == false {
		errCorrupt := errors.New(strings.ToTitle("Head and Tail has been tampered. >_<"))
		return errCorrupt
	}
	//Decrypt IDSlice
	IDSlice := userlib.SymDec(record.EncryptionKey, IDCombined[64:])
	Head := IDSlice[:16]
  Tail := IDSlice[16:]
  //userlib.DebugMsg("The encryptKey in record is: %v",record.EncryptionKey)

	//update UserData
	var keys [2][]byte
	keys[0] = record.EncryptionKey
  keys[1] = record.MacKey
	var ids  [2][]byte
	ids[0] = Head
  ids[1] = Tail
	userdata.KeysForFiles[filename] = keys
	userdata.UuidForFiles[filename] = ids

	//update and reupload user data
	d, _ := json.Marshal(userdata)
	userSlice := []byte(d)
	// Generate keys for encrypting the userdata.
	keyOne, keyTwo, keyThree, keyErr := userKeys(userdata.Username, userdata.Password)
	if keyErr != nil{
		return keyErr
	}
	// Encrypt the userslice
	IV := userlib.RandomBytes(16)
	userSliceEnc := userlib.SymEnc(keyOne, IV, userSlice)
	//calculate HMAC of encrypted user struct
	HMACofUser, errSig := userlib.HMACEval(keyThree, userSliceEnc)
	if errSig != nil{
		return errSig
	}
	//calculate the id to store user struct
	userID, errID := userlib.HMACEval(keyTwo, []byte(userdata.Username))
	if errID != nil || len(userID) < 16{
		return errID
	}
	//store the userstruct on Datastore
	stored := append(HMACofUser, userSliceEnc...)
	userlib.DatastoreSet(bytesToUUID(userID), stored)
	return nil
}

// Removes access for all others.
func (userdata *User) RevokeFile(filename string) (err error) {
	keys := userdata.KeysForFiles[filename]
	encryptKey, macKey := keys[0], keys[1]
	ids := userdata.UuidForFiles[filename]
	headSlice, tailSlice := ids[0], ids[1]
	if len(headSlice) < 16 || len(tailSlice) < 16{
		DataCorrupted := errors.New(strings.ToTitle("Invalid identification data for file."))
		return DataCorrupted
	}
	head := bytesToUUID(headSlice)
	tail := bytesToUUID(tailSlice)
	// Define errors.
	err404 := errors.New(strings.ToTitle("File does not exist. >_<"))
	errCorrupt := errors.New(strings.ToTitle("File corrupted!!!!! >_<"))
	//get the file from Datastore
	MacAndEncrypt, exist := userlib.DatastoreGet(head)
	if exist == false || len(MacAndEncrypt) < 64{
		return err404
	}
	//Verfiy that MAC is the same
	HMACget := MacAndEncrypt[:64]
	HMACcompute, HMACerr:= userlib.HMACEval(macKey, MacAndEncrypt[64:])
	if HMACerr != nil{
		return HMACerr
	}
	if userlib.HMACEqual(HMACget, HMACcompute) == false {
		return errCorrupt
	}
	// Begin to decrypt file content.
	fileSlice := userlib.SymDec(encryptKey, MacAndEncrypt[64:])
	var fileStruct FileNode
	json.Unmarshal(fileSlice, &fileStruct)
	next := fileStruct.Next
	data := fileStruct.FileContent
	//过河拆桥，把数据下载下来以后删掉它！
	userlib.DatastoreDelete(head)
	//you can now decrypt the tail
	tailVal, exist := userlib.DatastoreGet(tail)
	if exist == false || len(tailVal) < 64{
		return err404
	}
	HMACget = tailVal[:64]
	HMACcompute, HMACerr = userlib.HMACEval(macKey, tailVal[64:])
	if HMACerr != nil{
		return HMACerr
	}
	if userlib.HMACEqual(HMACget, HMACcompute) == false {
		return errCorrupt
	}
	//Then decrypt the tail value
	tailVal = userlib.SymDec(encryptKey, tailVal[64:])
	//begin to iterate through the linkedlist
	for testEq(next, tailVal) == false {
		nextasUUID := bytesToUUID(next)
		MacAndEncrypt, exist = userlib.DatastoreGet(nextasUUID)
		if exist == false || len(MacAndEncrypt) < 64{
			return  err404
		}
		//Verfiy that MAC is the same
		HMACget = MacAndEncrypt[:64]
		HMACcompute, HMACerr = userlib.HMACEval(macKey, MacAndEncrypt[64:])
		if HMACerr != nil{
			return HMACerr
		}
		if userlib.HMACEqual(HMACget, HMACcompute) == false {
			return errCorrupt
		}
		//Decrypt the current package.
		fileSlice = userlib.SymDec(encryptKey, MacAndEncrypt[64:])
		json.Unmarshal(fileSlice, &fileStruct)
		next = fileStruct.Next
		data = append(data, fileStruct.FileContent...)
		userlib.DatastoreDelete(nextasUUID)
	}


  //after deletion, clear the mapping in user struct and reupload user structs
	delete(userdata.KeysForFiles, filename)
	delete(userdata.KeysForFiles, filename)


	//update the User afterwards
	//UPDATING the user
	// Converting userdata from a struct to a byte slice.
	d, _ := json.Marshal(userdata)
	userSlice := []byte(d)
	// Generate keys for encrypting the userdata.
	keyOne, keyTwo, keyThree, keyErr := userKeys(userdata.Username, userdata.Password)
	if keyErr != nil{
		return
	}
	// Encrypt the userslice
	IV := userlib.RandomBytes(16)
	userSliceEnc := userlib.SymEnc(keyOne, IV, userSlice)
	//calculate HMAC of encrypted user struct
	HMACofUser, errSig := userlib.HMACEval(keyThree, userSliceEnc)
	if errSig != nil{
		return
	}
	//calculate the id to store user struct
	userID, errID := userlib.HMACEval(keyTwo, []byte(userdata.Username))
	if errID != nil || len(userID) < 16{
		return
	}
	//store the userstruct on Datastore
	stored := append(HMACofUser, userSliceEnc...)
	userlib.DatastoreSet(bytesToUUID(userID), stored)



	//reupload the file
	userdata.StoreFile(filename,data)
	return nil
}
