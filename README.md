# Pdf-Extractor
Pdf Extractor

Covert array list of object to pdf table

[![](https://jitpack.io/v/MostafaGad1911/Pdf-Extractor.svg)](https://jitpack.io/#MostafaGad1911/Pdf-Extractor)


# Examples :
``` kotlin 
       
    var userslist: ArrayList<User> = ArrayList()
    userslist.add(User(name = "Mostafa",age =  15 ,  country = "Egypt"))
    userslist.add(User(name = "Ahmed", age = 25 ,  country = "Egypt"))
    userslist.add(User(name = "Mohammed",age =  35 ,  country = "Egypt"))
    userslist.add(User(name = "Kareem", age = 40,  country = "Egypt"))

    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                val permission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permission, PdfExtractor.STORAGE_CODE)
            } else {
                extractPdf(list = userslist, onPdfExtracted = { it:PdfDocument

                })
            }

        } else {
            extractPdf(list = userslist , onPdfExtracted = { it:PdfDocument

            })
        }
```


# Getting Started 
## Step 1: Add it to build.gradle (project level) at the end of repositories:

 ``` kotlin  
             allprojects 
               {
	              repositories 
		           {	
			       maven { url 'https://jitpack.io' }
		           }  
	           }
```          
        

## Step 2 : Add the dependency
 ``` kotlin  
        implementation 'com.github.MostafaGad1911:Pdf-Extractor:1.0.0'
        
```         
## Notes
     Release #1.1.0 Support only english language , and primitive data types for table columns

## Example

 <img src="https://user-images.githubusercontent.com/25991597/156902014-920f36fb-3545-4e2a-a6d5-4230bd2fad0d.jpg"  width="300" height="550"   />
	   
