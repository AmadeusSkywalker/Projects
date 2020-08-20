package proj2

// You MUST NOT change what you import.  If you add ANY additional
// imports it will break the autograder, and we will be Very Upset.

import (
	"testing"
	"reflect"
	"github.com/nweaver/cs161-p2/userlib"
	_ "encoding/json"
	_ "encoding/hex"
	_ "github.com/google/uuid"
	_ "strings"
	_ "errors"
	_ "strconv"
)


func TestInit(t *testing.T) {
	t.Log("Initialization test")

	// You may want to turn it off someday
	userlib.SetDebugStatus(true)
	//someUsefulThings()
	userlib.SetDebugStatus(false)
	u, err := InitUser("alice", "fubar")
	if err != nil {
		// t.Error says the test fails
		t.Error("Failed to initialize user", err)
	}
	u, err = InitUser("", "")
	if err == nil {
		// t.Error says the test fails
		t.Error("Failed to recognize invalid string", err)
	}
	u, err = InitUser("alice", "")
	if err == nil {
		// t.Error says the test fails
		t.Error("Failed to recognize username has been taken.", err)
	}
	u, err = InitUser("alice1234567890123456789123456789", "")
	if err == nil {
		// t.Error says the test fails
		t.Error("Failed to recognize username is not good.", err)
	}
	u, err = InitUser("", "alice1234567890123456789123456789")
	if err == nil {
		// t.Error says the test fails
		t.Error("Failed to recognize password is not good.", err)
	}
	// t.Log() only produces output if you run with "go test -v"
	t.Log("Got user", u)
	// If you want to comment the line above,
	// write _ = u here to make the compiler happy
	// You probably want many more tests here.
}

func TestGetUser(t *testing.T){
	t.Log("GetUser test")
	x, secondError := GetUser("alice", "fubar")
	if secondError != nil {
		// t.Error says the test fails
		t.Error("Failed to Get user", secondError)
	}
	x, secondError = GetUser(" ", "fubar")
	if secondError == nil {
		// t.Error says the test fails
		t.Error("Failed to recognize invalid string", secondError)
	}
	x, secondError = GetUser("alice", "fu")
	if secondError == nil {
		// t.Error says the test fails
		t.Error("Failed to recognize invalid password", secondError)
	}
	t.Log("Got user", x)
}

func TestStorage(t *testing.T) {
	// And some more tests, because
	u, err := GetUser("alice", "fubar")
	if err != nil {
		t.Error("Failed to reload user", err)
		return
	}
	//t.Log("Loaded user", u)
  	userlib.SetDebugStatus(true)
	v := []byte("This is a test")
	v1 := []byte("This is a test2")
	u.StoreFile("file1", v)



	v2, err2 := u.LoadFile("file1")
	if err2 != nil {
		t.Error("Failed to upload and download", err2)
	}
	//userlib.SetDebugStatus(true)
	if !reflect.DeepEqual(v, v2) {
		t.Error("Downloaded file is not the same", v, v2)
	}
	// Check invalid file name.
	u.StoreFile("", v)
	v2, err2 = u.LoadFile("")
	if err2 == nil {
		t.Error("Failed to recognize invalid string", err2)
	}
	u.StoreFile("file2", v1)
	result1, err1 := u.LoadFile("file2")
	if err1 != nil {
		t.Error("Failed to save another file", err1)
	}
	if !reflect.DeepEqual(v1, result1) {
		t.Error("Downloaded file is not the same", v1, result1)
	}
	u.StoreFile("file1", v1)
	result2, err2 := u.LoadFile("file2")
	if err1 != nil {
		t.Error("Failed to save another file", err1)
	}
	if !reflect.DeepEqual(v1, result2) {
		t.Error("Downloaded file is not the same", v1, result2)
	}
}


func TestAppend(t *testing.T) {
	// And some more tests, because
	u, err := GetUser("alice", "fubar")
	if err != nil {
		t.Error("Failed to reload user", err)
		return
	}
	// Check invalid file name.
	v := []byte("This is a test")
	u.StoreFile("file1", v)
	err = u.AppendFile("", v)
	if err == nil {
		t.Error("Failed to recognize invalid string", err)
	}
	//Add a node
	u.AppendFile("file1",[]byte(" but shit happens"))
	v1 := []byte("This is a test but shit happens")
	v2, err2 := u.LoadFile("file1")
	if err2 != nil {
		t.Error("Failed to upload and download", err2)
	}
	err3 := u.AppendFile("",[]byte(" but shit happens"))
	if err3 == nil {
		t.Error("Should not append to empty.", err3)
	}
	//userlib.SetDebugStatus(true)
	if !reflect.DeepEqual(v1, v2) {
		t.Error("Downloaded file is not the same", v, v2)
	}
	//Add another node!
	u.AppendFile("file1",[]byte(" I goy ya Man!"))
	v = []byte("This is a test but shit happens I goy ya Man!")
	v2, err2 = u.LoadFile("file1")
	if err2 != nil {
		t.Error("Failed to upload and download", err2)
	}
	//userlib.SetDebugStatus(true)
	if !reflect.DeepEqual(v, v2) {
		t.Error("Downloaded file is not the same", v, v2)
	}
}


func TestShare(t *testing.T) {
	userlib.SetDebugStatus(true)
	u, err := GetUser("alice", "fubar")
	if err != nil {
		t.Error("Failed to reload user", err)
	}
	u2, err2 := InitUser("bob", "foobar")
	if err2 != nil {
		t.Error("Failed to initialize bob", err2)
	}

	var v, v2 []byte
	var magic_string string
  //userlib.DebugMsg("CHECKPOINT0")
	v = []byte("This is a test")
	u.StoreFile("file1", v)
	v, err = u.LoadFile("file1")
	if err != nil {
		t.Error("Failed to download the file from alice", err)
	}
	//userlib.DebugMsg("CHECKPOINT1")
	magic_string, err = u.ShareFile("file1", "bob")
	if err != nil {
		t.Error("Failed to share the a file", err)
	}
	magic_string_not_good, err_not_good := u.ShareFile("file1", "ccc")
	if err_not_good == nil {
		t.Error("Should not be able to share to nonexistent user", magic_string_not_good)
	}
	//userlib.DebugMsg("CHECKPOINT2")
	err = u2.ReceiveFile("file2", "alice", magic_string)
	if err != nil {
		t.Error("Failed to receive the share message", err)
	}
	err = u2.ReceiveFile("file3", "alice", "magic_string")
	if err == nil {
		t.Error("Should not receive the share message", err)
	}
	err = u2.ReceiveFile("file4", "bob", magic_string)
	if err == nil {
		t.Error("Should not receive the share message", err)
	}
	//userlib.DebugMsg("CHECKPOINT3")
	v2, err = u2.LoadFile("file2")
	//userlib.DebugMsg("CHECKPOINT4")
	if err != nil {
		t.Error("Failed to download the file after sharing", err)
	}
	v = []byte("This is a test")
	if !reflect.DeepEqual(v, v2) {
		t.Error("Shared file is not the same", v, v2)
	}
}



	func TestRevoke(t *testing.T) {
		t.Log("Revoke test")
		//Get Alice and retain a copy of file1
		u, err := GetUser("alice", "fubar")
		if err != nil {
			t.Error("Failed to reload user", err)
		}
		v, err := u.LoadFile("file1")
		if err != nil {
			t.Error("Failed to download the file from alice", err)
		}
		//stupid bob shouldn't get file1
		u2, err2 := GetUser("bob", "foobar")
		if err2 != nil {
			t.Error("Failed to reload user", err2)
		}
		err = u.RevokeFile("file1")
		if err != nil {
			t.Error("Revoke fails because of", err)
		}
		_, err = u2.LoadFile("file2")
		if err == nil{
			t.Error("Bob should get an error!")
		}
		//Alice should still be able to get file1
		v2, err := u.LoadFile("file1")
		//userlib.DebugMsg("CHECKPOINT4")
		if err != nil {
			t.Error("Failed to download the file after revoking", err)
		}
		if !reflect.DeepEqual(v, v2) {
			t.Error("File after revoke isn't the same! Dummy!", v, v2)
		}
	}

	func TestDummy(t *testing.T){
		t.Log("dummy test")
		z := Dummy(1,2)
		if z != 3 {
			t.Error("simple calculation wrong")
		}
		z = Dummy(2,1)
		if z != 1 {
			t.Error("simple calculation wrong")
		}
		z = Dummy2(1)
		if z != 1{
			t.Error("should be 1")
		}
		z = Dummy2(2)
		if z != 2{
			t.Error("Should be 2")
		}
		z = Dummy2(3)
		if z != 3{
			t.Error("Should be 3")
		}
		z = Dummy2(4)
		if z != 4{
			t.Error("Should be 4")
		}
		z = Dummy2(5)
		if z != 5{
			t.Error("Should be 5")
		}
		z = Dummy2(6)
		if z != 6{
			t.Error("Should be 6")
		}
		z = Dummy2(7)
		if z != 7{
			t.Error("Should be 7")
		}
		z = Dummy2(8)
		if z != 8{
			t.Error("Should be 8")
		}
		z = Dummy2(9)
		if z != 9{
			t.Error("Should be 9")
		}
		z = Dummy2(10)
		if z != 10{
			t.Error("Should be 10")
		}
		z = Dummy2(11)
		if z != 11{
			t.Error("Should be 11")
		}
		z = Dummy2(12)
		if z != 12{
			t.Error("Should be 12")
		}
		z = Dummy2(13)
		if z != 13{
			t.Error("Should be 13")
		}
		z = Dummy2(14)
		if z != 14{
			t.Error("Should be 14")
		}
		z = Dummy2(15)
		if z != 15{
			t.Error("Should be 15")
		}
		z = Dummy2(50)
		if z != 2500{
			t.Error("Should be 2500")
		}
	}



		func TestComprehensive(t *testing.T) {
			t.Log("Comprehensive test")
			//initialize the users
			u, err := InitUser("amadeus", "stupidnick")
			if err != nil {
				t.Error("Failed to initialize user", err)
			}
			t.Log("Create user amadeus", u)
			u2, err := InitUser("sarah", "beautifulpopa")
			if err != nil {
				t.Error("Failed to initialize user", err)
			}
			t.Log("create user sarah", u2)
			u3, err := InitUser("eve", "stupidnick")
			if err != nil {
				t.Error("Failed to initialize user", err)
			}
			t.Log("create user eve", u3)
			//Incorrect password or username for Amadeus
			amadeus, Error := GetUser("amadeus", "fubar")
			if Error == nil {
				t.Error("User password should be incorrect", Error)
			}
			amadeus, Error = GetUser("amadeus1", "stupidnick")
			if Error == nil {
				t.Error("there isn't such a user", Error)
			}
			//Correctly get all the users
			amadeus, Error = GetUser("amadeus", "stupidnick")
			if Error != nil {
				t.Error("should get a user", Error)
			}
			sarah, Error := GetUser("sarah", "beautifulpopa")
			if Error != nil {
				t.Error("Didn't get user successfully", Error)
			}
			eve, Error := GetUser("eve", "stupidnick")
			if Error != nil {
				t.Error("Didn't get user successfully", Error)
			}
      t.Log("Got user eve", eve)
			//amadeus stores a file
			v := []byte("This is a design document")
			amadeus.StoreFile("DesignDoc", v)
			v2, err2 := amadeus.LoadFile("DesignDoc")
			if err2 != nil {
				t.Error("Failed to upload and download", err2)
			}
			if !reflect.DeepEqual(v, v2) {
				t.Error("Downloaded file is not the same", v, v2)
			}
			//Amadeus then shares the design document with Sarah
			var magic_string string
			magic_string, err = amadeus.ShareFile("DesignDoc", "sarah")
			if err != nil {
				t.Error("Failed to share a file", err)
			}
			err = sarah.ReceiveFile("DesignDoc", "amadeus", magic_string)
			if err != nil {
				t.Error("Failed to receive the share message", err)
			}
			v2, err = sarah.LoadFile("DesignDoc")
			if err != nil {
				t.Error("Failed to download the file after sharing", err)
			}
			if !reflect.DeepEqual(v, v2) {
				t.Error("Shared file is not the same", v, v2)
			}
			err = sarah.ReceiveFile("DesignDoc", "amadeus", magic_string)
			//Eve tries to receive file with magic string
			err = eve.ReceiveFile("DesignDoc", "amadeus", magic_string)
			if err == nil{
				t.Error("Eve shouldn't receive the file", err)
			}
			//Eve tries to access the file DesignDoc and gets rejected
			v3, err := eve.LoadFile("DesignDoc")
			if err == nil || len(v3) > 0 {
				t.Error("Eve shouldn't get her hands on the deignDoc", err)
			}
			//Eve is really angry and creates a file with the same filename and same content
			vEve := []byte("This is a design document")
			eve.StoreFile("DesignDoc", vEve)
			v3, err = eve.LoadFile("DesignDoc")
			if err2 != nil {
				t.Error("Failed to upload and download", err2)
			}
			if !reflect.DeepEqual(vEve, v3) {
				t.Error("Downloaded file is not the same", vEve, v3)
			}
			//Sarah changes the file and Amadeus needs to access it
			sarah.AppendFile("DesignDoc",[]byte(" but shit happens"))
			v = []byte("This is a design document but shit happens")
			v1, err := amadeus.LoadFile("DesignDoc")
			if err != nil {
				t.Error("Failed to upload and download", err)
			}
			if !reflect.DeepEqual(v, v1) {
				t.Error("Amadeus gets a differnt file after sarah changes it", v, v1)
			}
			//Then amadeus changes the file, hoping that sarah can get it
      amadeus.AppendFile("DesignDoc",[]byte(" and nick really sucks"))
      v = []byte("This is a design document but shit happens and nick really sucks")
			v1, err = sarah.LoadFile("DesignDoc")
			if err != nil {
				t.Error("Failed to upload and download", err)
			}
			if !reflect.DeepEqual(v, v1) {
				t.Error("Amadeus gets a differnt file after sarah changes it", v, v1)
			}
      		//Amadeus pities Eve, now decide to let Sarah share the file with her
			magic_string, err = sarah.ShareFile("DesignDoc", "eve")
			if err != nil {
				t.Error("Failed to share a file", err)
			}
			err = eve.ReceiveFile("DesignDoc1", "sarah", magic_string)
			if err != nil {
				t.Error("Failed to receive the share message", err)
			}
			v2, err = eve.LoadFile("DesignDoc1")
			if err != nil {
				t.Error("Failed to download the file after sharing", err)
			}
			if !reflect.DeepEqual(v, v2) {
				t.Error("Shared file is not the same", v, v2)
			}
			//Eve doesn't accept Amadeus's hospitality, and appends curse words
			//Sarah sees the file, and laughs
			eve.AppendFile("DesignDoc1",[]byte(" amadeus you are an idiot"))
      		v = []byte("This is a design document but shit happens and nick really sucks amadeus you are an idiot")
			v1, err = sarah.LoadFile("DesignDoc")
			if err != nil {
				t.Error("Failed to upload and download", err)
			}
			if !reflect.DeepEqual(v, v1) {
				t.Error("Amadeus gets a differnt file after sarah changes it", v, v1)
			}
			//Amadeus got angry, and revoke the access of this file.
      		err = amadeus.RevokeFile("DesignDoc")
			if err != nil {
				t.Error("Amadeus should be able to revoke the file", err)
			}
			//Amadeus should only be only be revoke the file created.
			err = amadeus.RevokeFile("Design")
			if err == nil {
				t.Error("Amadeus should not be able to revoke the file", err)
			}
			//Eve can't get hands on the design document any more,
			//but she has her own design document
			v3, err = eve.LoadFile("DesignDoc1")
			if err == nil || len(v3) > 0 {
				t.Error("Eve shouldn't get her hands on the deignDoc", err)
			}
			err = eve.RevokeFile("DesignDoc1")
			if err == nil || len(v3) > 0 {
				t.Error("Eve shouldn't get her hands on the deignDoc", err)
			}
			v3, err = eve.LoadFile("DesignDoc")
			if err != nil {
				t.Error("Eve should still get her own design document", err)
			}
			//sarah can no longer get the design document, and she cries
			v3, err = sarah.LoadFile("DesignDoc")
			if err == nil || len(v3) > 0 {
				t.Error("Sarah should cry", err)
			}
			//Amadeus is the only one left with the original design document because he is awesome
			v3, err = amadeus.LoadFile("DesignDoc")
			if err != nil {
				t.Error("Amadeus should be awesome", err)
			}
			if !reflect.DeepEqual(v, v3) {
				t.Error("amadeus should get the same file", v, v3)
			}
		}
