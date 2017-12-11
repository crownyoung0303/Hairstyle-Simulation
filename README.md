# EC601 Project Hairstyle Simulation

###### Team member: Lingxiu Ge, Lingshan Yang, Tong Ye, Yujia Wang

Haitstyle Simulation is a project designed for 
- Users who have difficulties expressing desired hairstyles to their barbers
- Users who want to know which hairstyle fits them best 
- Users who want to know how they look with different hairstyles.
- Barbers who want to help their customers looking for an ideal hairstyle
- Barbers who want to show their customers different hairstyles without actually cutting their hair



### Accomplishments:
We successfully implemented an Android application that -
- helps users who want to know which hairstyle fits them best or how they look with different hairstyles.
- Recognizes human face and establishes key feature points.
- Provides real-time Augmented-Reality 3D hairstyle simulation.
- Recommends hairstyle to users based on different face shapes.



### How to run our app:
1. install andriod studio
2. import project
3. run project on an andriod phone (please note that our project DOES NOT work on virtual phone)
4. open main and choose a type of hair style
5. our app works the best when the user stays close to the front camera
6. Note that you may encounter "Invalid VCS root mapping" Errors but they are fine just ignore them.


### Recommendation:
<img align="center" width="250" src=rec_alg.png>


Base on the key feature points (graph above), we use two parameters to define a person’s face type:
- p1=dis(1,15)/dis(6,10)
  * If p1<2.0, the face is recognized as an oval shape.
  * If 2.0<p1<2.5, the face is recognized as a round shape 
  * If p1>2.5, the face is recognized as a square shape
- p2=dis(0,16)/dis(8,27)
  * If p2<1.0, the face is recognized as short
  * If 1.0<p2<1.15, the face is recognized as medium length 
  * If p2>1.15, the face is recognized long
  
We are still working on getting more 3D hairstyle models to work for our project. For now, we only have two hairstyles that works well. We have implemented multiple colors for the hairstyles and created a recommendation module based on different hair colors. We have nine types of hairstyles and colors. If you click on the recommendation button, the recommended hairstyle based on our algorithm will show up. PLEASE note that you need to click on the recommendation button everytime the user is changed (a.k.a different faces). If the recommendation button is not clicked and user is changed, the app will keep showing the recommendation hairstyle for the previous user until the button is clicked again.<br />

### Technology Used
- Use Android studio to develop Android application
- Use STMobile’s built-in function to recognize a human face and establish 106 (at most) key feature points.
- Use Rajawali to generate and present 3D model
- Use blender to edit 3D models


### Test Report
<img align="center" src=test_results.png>

======================================================================================<br />
#### Thanks for using Hairstyle Simulation.
#### If you have any questions, feel free to email us at glxlily@bu.edu or yls@bu.edu.
