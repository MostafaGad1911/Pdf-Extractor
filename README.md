# Pdf-Extractor
Pdf Extractor

Covert array list of object to pdf table

[![](https://jitpack.io/v/MostafaGad1911/Pdf-Extractor.svg)](https://jitpack.io/#MostafaGad1911/Pdf-Extractor)


# Examples :
``` kotlin 

     var user = User()
        user._1name = "Mostafa"
        user._2age = 28
        user._3country = "Egypt"
        user._4image = "https://png.pngtree.com/png-clipart/20210418/original/pngtree-lettering-ramadan-calligraphy-sticker-arabic-marhaban-ya-ramadhan-kareem-text-png-image_6237398.jpg"


        usersList.add(user)
        usersList.add(user)
        usersList.add(user)
        usersList.add(user)
        usersList.add(user)
        usersList.add(user)
        usersList.add(user)
        usersList.add(user)
        usersList.add(user)

        val headers: ArrayList<String> = ArrayList()
        headers.add("الإســم")
        headers.add("العمر")
        headers.add("العـنوان")
        headers.add("الصورة")


    if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            val permission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            requestPermissions(permission, 212)
        } else {
            PdfExtractor().Builder()
                .setDocsName("Gad")
                .setDocumentTitle("Gad Title")
                .setHeaders(headers)
                .setDocumentContent(usersList)
                .setCellColor(R.color.orange)
                .setHeaderColor(R.color.blue)
                .setCellTextColor(R.color.blue)
                .setHeaderTextColor(R.color.white)
                .setTableDirection(ExtractorLTR)
                .setLoadingColor(R.color.blue)
                .build(this)

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
        implementation 'com.github.MostafaGad1911:Pdf-Extractor:1.1.0'
        
```         
## Notes
     Release 1.1.1 images displayed only through url 

## Example

 <img src="https://user-images.githubusercontent.com/25991597/156902014-920f36fb-3545-4e2a-a6d5-4230bd2fad0d.jpg"  width="300" height="550"   />
	   
