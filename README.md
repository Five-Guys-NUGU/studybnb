# StudyBNB
HYU AI/SE project 21-2 \
Learning, friend-like software 'StudyBNB'

## Members
- Kim Useong, jeneve1@hanyang.ac.kr
- Park Hankyu, official03x@hanyang.ac.kr
- Lim Dongjin, ehdwlsdudwo1@hanyang.ac.kr
- Chi Sangyun, csy9604@hanyang.ac.kr
- Michael Sklors, skmi1013@h-ka.de

## Links
- Notion page: https://ancient-fighter-d30.notion.site/StudyBNB-c63a5795d9de42758a436764d5bca04d
- Explanation & Demo Video: 
- Documentation: 

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
### Forest
### Focus To-DO: Pomodoro Timer
### QandA
### AI TOEIC - RIIID Tutor


## Development Environment
Model | Processor | RAM | OS |
--- | --- | --- | --- |
MacBook Pro | Intel Core i9, 2.4GHz octa-core | 32GB 2667 MHz DDR4 | macOS Catalina(10.15.7)
MacBook Air | Intel Core i5, 1.6GHz dual-core | 4GB 1600MHz DDR4 | macOS  Catalina(10.15.7)
Dell  XPS  15  9570 | Intel  Core  i7-8750h,  2.20GHz | 16GB  2208MHz  DDR4 | Windows 10(10.0.18363 Numero 18363)
HP  Spectre  x360  Convertible | Intel  Core  i7-8550U,  1.80GHz | 16GB  2208MHz  DDR4 | Windows 10(10.0.18363 Numero 18363



## Overall Architecture
![image](https://user-images.githubusercontent.com/49526312/145183348-75c0a4d4-2426-4faa-9aec-afa82986507b.png)
The figure displays that StudyBNB consists of three parts: Frontend, Backend and external Cloud. The frontend focuses on the visual elements of the app that the user interacts with while the backend focuses on the server side that the user can't see. The external cloud provides interaction with external services.

Frontend is represented by the mobile app and the NUGU speaker. The former one allows interaction with the modules "login", "timer", "notes" and "settings". The "login" function makes use of Google sign-in and communicates with the Google API. NUGU Speaker needs to work with the NUGU API to use functions of the mobile app. 

Backend is represented by the cloud consisting of the backend capabilities of Firebase and its Firestore database. The functions from the frontend save and retrieve data in Firebase. Another part of the backend is the app server which is built on the Python web-framework Flask. The app server has an timer function that uses words from the NUGU speaker and data from Firebase as input and starts the timer. For machine learning capabilities, data from the Firestore database is used to train the model for image recognition and matching users capabilities. Inside the app server the model will be applied for the notes function.

## Methodology
### A. StudyMate

- 왜 K-mean Clustering인가
    
    저희는 매칭 알고리즘으로 K-mean Clustering 기법을 사용하였습니다. 저희는 많은 사용자들 중 같은 과목을 학습하는 사용자들 중 가장 유사한 두 사용자를 하나의 매칭 그룹으로 만들고 싶었습니다. 사용자 정보에 대한 label이 따로 존재하지 않았으므로, 저희는 머신러닝 기법들 중 Clustering을 이용하여 사용자들을 여러 군집으로 나눈 후, 각각의 군집에서 무작위로 두 사용자를 택하여 매칭시키는 방법을 택하기로 하였습니다.
    
    Clustering 기법들 중 하나인 K-mean Clustering 기법은 군집의 수를 미리 정할 필요가 없고, 유클리드 거리 기반으로 유사 군집을 탐색하기 때문에 저희가 원하는 바대로, 유사도 높은 두 개체를 매칭시킨다는 목적에 적합하다고 생각했습니다.
    
- 데이터 정규화
    
    raw data를 그대로 Clustering에 사용하는 것과 데이터 정규화를 거치고 난 후 Clustering을 진행하는 것은 큰 차이가 있습니다. raw data를 그대로 사용할 경우, scale이 더 큰 feature의 영향력이 그렇지 않은 feature의 영향력보다 지나치게 커질 수 있습니다. 우리는 그러한 현상을 방지하고자 정규화 과정을 거치기로 했습니다.
    
    ```python
    sdscaler = StandardScaler()
    df[['total_study_time', 'avg_study_time']] = sdscaler.fit_transform(df[['total_study_time', 'avg_study_time']])
    ```
    
- 최적의 K 찾기
    
    군집의 수를 미리 정하지 않아도 K-mean Clustering은 동작하지만, 만약 K의 범위를 정하지 않는다면 두 가지 문제가 생길 수 있습니다. 만일 K가 너무 적은 숫자가 될 경우 거의 모든 사용자가 같은 군집에 속하게 되어, 이 매칭 알고리즘을 돌리는 이유가 없어질 지도 모릅니다. 반대로 K가 너무 큰 숫자가 될 경우 사용자들이 매우 많은 군집으로 분화되어 많은 사용자가 매칭이 이루어지지 않는 경우가 많이 생길 수도 있습니다. 한 군집에 사용자가 홀수 명 있을 경우 한 명의 사용자는 매칭에 실패하게 되기 때문입니다.
    
    따라서 저희는 K의 범위를 정하고 그 범위 내에서 전체 사용자를 가장 특색 있는 군집으로 나눌 수 있는, 최적의 K를 찾기로 했습니다. K가 너무 적은 값도 너무 큰 값도 아닌 적당한 값을 가지게 하기 위해, 2 $≤$ K $≤$ $round(\sqrt{n})$ 의 범위를 만족하는 자연수 K 중 가장 높은 Silhouette Score를 가지는 K를 최적의 K로 간주하기로 하였습니다.
    
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
    
    찾아낸 최적의 K를 이용하여, K-mean Clustering을 진행하였습니다.
    
    ```python
    def kmean(df, k):
        kmeans = KMeans(n_clusters = k).fit(df[['total_study_time', 'avg_study_time']])
        labels = kmeans.labels_
        return labels
    ```
    
- 각각의 Cluster 내에서 매칭하기
    
    Clustering 결과인 각각의 군집 내에서 랜덤하게 두 사용자를 골라 매칭시켰습니다. 만약, 군집이 홀수 명의 사용자로 구성된다면, 마지막 한 명의 사용자는 매칭에 실패하게 됩니다.
    
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
    
    앱 내에서 OCR의 구현 방식은 다음과 같습니다. 이미지 속 텍스트를 인식하기 위해선 `Bitmap` 또는 `media.Image`, `ByteBuffer`, 바이트 배열 또는 기기의 파일에서 `FirebaseVisionImage` 객체를 만듭니다. 그런 다음 `FirebaseVisionImage` 객체를 `FirebaseVisionTextRecognizer`의 `processImage` 메서드에 전달하고, 태스크가 수행되면 이미지는 텍스트로 변환됩니다.
    

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
