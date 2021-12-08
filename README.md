# StudyBNB
HYU AI/SE project 21-2 \
Friend-like software that helps us study efficiently 'StudyBNB'

## Members
- Kim Useong, jeneve1@hanyang.ac.kr
- Park Hankyu, official03x@hanyang.ac.kr
- Lim Dongjin, ehdwlsdudwo1@hanyang.ac.kr
- Chi Sangyun, csy9604@hanyang.ac.kr
- Michael Sklors, skmi1013@h-ka.de

## Links
- Notion page: https://ancient-fighter-d30.notion.site/StudyBNB-c63a5795d9de42758a436764d5bca04d
- Explanation & Demo Video: 
- Documentation: https://github.com/Five-Guys-NUGU/documentation

## Proposal
Studying and reading is always present in our daily life, especially as a college student. When we study, it is easy to lose track of the hours spent and forgetting to take breaks. This results in being inefficient for the goal of studying for a class or an exam. Conversely, it is easy to lose concentration due to the temptation to use laptops and cell phones. From these cases, we thought that it would be good to students to provide a time-check application for studying. By using our application, students can see whether they should have rest at proper time, and they can focus on their study more. The ultimate question to answer is always "How can I be more efficient with my time for studying?" 

### Problematic Situation
- Can't check own study time exactly
- Memorization that relies only on sight
### Solution
- Study Time Checker
- Memorizing notes using speakers

## Related software and research
### Yeolpumta(열품타)
- https://play.google.com/store/apps/details?id=com.pallo.passiontimerscoped
### Forest
- https://play.google.com/store/apps/details?id=cc.forestapp
### Focus To-DO: Pomodoro Timer
- https://play.google.com/store/apps/details?id=com.superelement.pomodoro
### QandA
- https://play.google.com/store/apps/details?id=com.mathpresso.qanda
### AI TOEIC - RIIID Tutor
- https://play.google.com/store/apps/details?id=co.riiid.vida

## Development Environment



## Overall Architecture
![image](https://user-images.githubusercontent.com/49526312/145183348-75c0a4d4-2426-4faa-9aec-afa82986507b.png)
The figure displays that StudyBNB consists of three parts: Frontend, Backend and external Cloud. The frontend focuses on the visual elements of the app that the user interacts with while the backend focuses on the server side that the user can't see. The external cloud provides interaction with external services.

Frontend is represented by the mobile app and the NUGU speaker. The former one allows interaction with the modules "login", "timer", "notes" and "settings". The "login" function makes use of Google sign-in and communicates with the Google API. NUGU Speaker needs to work with the NUGU API to use functions of the mobile app. 

Backend is represented by the cloud consisting of the backend capabilities of Firebase and its Firestore database. The functions from the frontend save and retrieve data in Firebase. Another part of the backend is the app server which is built on the Python web-framework Flask. The app server has an timer function that uses words from the NUGU speaker and data from Firebase as input and starts the timer. For machine learning capabilities, data from the Firestore database is used to train the model for image recognition and matching users capabilities. Inside the app server the model will be applied for the notes function.

## Methodology
### A. StudyMate

- Why is it K-mean Clustering?
    
    We used 'K-mean clustering' as a matching algorithm. We wanted to make two of the most similar users among those learning the same subject into one matching group. Since there was no label that indicates user information, we decided to divide users into several clusters using clustering among machine learning techniques and randomly select and match two users in each cluster.

    One of the clustering techniques, the K-mean clustering technique, does not need to set the number of clusters in advance and searches for similar clusters based on Euclidean distance, so we thought it was suitable for the purpose of matching two highly similar objects.
    
- Data Normalization
    
    There is a big difference between using raw data as it is for clustering and proceeding with clustering after data normalization. If raw data is used as it is, the influence of features with a larger scale can be too large than those of features that do not. We decided to go through a normalization process to prevent such a phenomenon.
    
    ```python
    sdscaler = StandardScaler()
    df[['total_study_time', 'avg_study_time']] = sdscaler.fit_transform(df[['total_study_time', 'avg_study_time']])
    ```
    
- Finding Best K
    
    K-mean clustering works even if the number of clusters is not determined in advance, but if the range of K is not determined, there can be two problems. If K becomes too small a number, almost all users will belong to the same cluster, and there may be no reason to turn this matching algorithm around. Conversely, if K becomes too large a number, users may differentiate into very large clusters, and many users may not be matched. This is because if there are odd numbers of users in one cluster, one user will fail to match.

    Therefore, we decided to determine the range of K and find the optimal K that can divide all users into the most distinctive clusters within that range. To ensure that K has a reasonable value, not too little or too large, we consider K with the highest Silhouette Score among natural numbers K satisfying the range of 2 <= K <= `round(root(N))` as optimal K.    

    ```python
    def findBestK(df):
        sil = []
        kmax = round(math.sqrt(len(df)))
    
        for k in range(2, kmax + 1):
            kmeans = KMeans(n_clusters = k).fit(df[['total_study_time', 'avg_study_time']])
            labels = kmeans.labels_
            sil.append(silhouette_score(df[['total_study_time', 'avg_study_time']], labels, metric = 'euclidean'))
    
        return (2 + sil.index(max(sil)))
    ```
    
- K-mean Clustering
    
    K-mean clustering was conducted using the optimal K found.
    
    ```python
    def kmean(df, k):
        kmeans = KMeans(n_clusters = k).fit(df[['total_study_time', 'avg_study_time']])
        labels = kmeans.labels_
        return labels
    ```
    
- Matching inside each clusters
    
    Two users were randomly selected and matched within each cluster of clustering results. If the cluster consists of an odd number of users, the last one user will fail to match.
    
    ```python
    def match(lists):
        couples = []
    
        for list in lists:
            if len(list) % 2:
                list.pop()
            random.shuffle(list)
            for i in range(0, len(list), 2):
                temp_arr = []
                temp_arr.append(df_uid[list[i]])
                temp_arr.append(df_uid[list[i+1]])
                couples.append(temp_arr)
    
        return couples
    ```
    

### B. Note-taking

- OCR in Android Application
    
    The implementation method of OCR within the app is as follows. To recognize the text in the image, create an object called `Bitmap`, `media.image`, `ByteBuffer`, and `Firebase VisionImage` from a byte array or device file. The `Firebase VisionImage` object is then forwarded to the `processImage` method of `Firebase VisionTextRecognizer`, and when the task is performed, the image is converted into text.

    

```kotlin
if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){

            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            if(bitmap != null){
                img_btn.setImageBitmap(bitmap)
            }else{
                val icon = BitmapFactory.decodeResource(getResources(), R.drawable.photo_default)
                img_btn.setImageBitmap(icon)
            }

            // Create a FirebaseVisionImage object from your image/bitmap.
            val firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap!!)
            val firebaseVision = FirebaseVision.getInstance()
            val firebaseVisionTextRecognizer = firebaseVision.onDeviceTextRecognizer

            // Process the Image
            val task = firebaseVisionTextRecognizer.processImage(firebaseVisionImage)

            task.addOnSuccessListener { firebaseVisionText: FirebaseVisionText ->
                //Set recognized text from image in our TextView
                val text = firebaseVisionText.text
                contents_view!!.setText(text)
        }
    }
```
## Evaluation & Analysis
### A. StudyMate

- Before / After Normalization
    
    Before normalization, the scale of 'total_study_time' is very large compared to 'avg_study_time', whereas after normalization, the two features show the same scale.
    
    Before Normalization             |  After Normalization
    :-------------------------:|:-------------------------:
    ![image](https://user-images.githubusercontent.com/49526312/145191795-9cc58ede-9a2b-4f8a-8da1-0d8d7df27e27.png)  |  ![image](https://user-images.githubusercontent.com/49526312/145191813-b44c5313-8e3c-415f-828a-a8dd9bb057d0.png)
- Silhouette Score
    
    Silhouette Score, which determines the best K, shows that it is better to set it to K=4 for the case.
    
    ![image](https://user-images.githubusercontent.com/49526312/145191706-aa410883-c4dc-4fc9-b305-e63c0f83db5c.png)
    
- K-mean Clustering Result
    
    This graph visualizes the results of K-mean clustering. Each cluster is expressed in a different color.
    
    ![image](https://user-images.githubusercontent.com/49526312/145191624-0ab0cb22-bfd6-41b1-aee5-bf75f42db9d7.png)


### B. Note-taking

- Data recognition accuracy was checked by comparing/analyzing the similarity between the text in the image and the text converted into the image.
- As a result of analyzing the accuracy of data recognition by collecting about 30 pieces of data, it showed about 98.6% accuracy.

## Use cases
### Mobile application


### NUGU AI Speaker


## User guide
### Installation

### Deletion

## Conclusion
When we first started our development plan, we planned to provide two paths: smartphone apps and AI speakers with two AI-based services: StudyMate and Note-taking.

However, due to limited development capabilities and time constraints, we had no choice but to produce slightly modified results from the initial plan. Among the many parts, the most regrettable thing is that the StudyMate function was not fully implemented in the app. If the service can be expanded as planned, I think we can make it more functionally completed software.
