# Tutorial Adpro

<details>
<summary> Modul-1-Coding-Standards </summary>

## Reflection 1

My code uses clear descriptive variables and functions. For example, the function `isQuantityInteger` clearly shows that it is a function that will check whether or not the quantity is and integer. The return value of the function also matches the phrasing of the function. If it returns true, then the quantity is an integer, and vice versa. I've also implemented the DRY concept, especially on functional tests. For example when trying to test for creating a new product with various inputs, I created a template called `testInvalidCreateProductTemplate` which accepts a driver, the quantity , and the expected error message, that way, I don't have to rewrite the same code over and over again. I also added input validation for creating and updating products, specifically on the quantity field. The input validation checks that the quantity is an integer and that it is > 0. All the check is done in the server side and if a user inputs an invalid quantity, rather than the program crashing, it will display a notification to the user using red color.

## Reflection 2

1. 

The unit tests help me feel more certain that my program is working as expected. In general, one class should contain unit tests for 1 feature. We should test both positive and negative cases as well as any errors that might appear in the code. One metric to check if our testcase is enough is through code coverage. However, 100% code coverage doesnt mean our code is bug free because there might be some errors we forgot to handle. For example, if we make a division function but forget to handle division by zero and our testcase doesn't check for it, it will still have 100% code coverage.

2.  

If we repeat the exact same setup the code quality will reduce because we are not following the DRY principle. DRY principle states that we should not repeat our code, instead its better to package that code into a function then call that function whenever we need it. For unit tests, we could create a new java file which will contain the code and functions to setup the unit tests, then all future unit tests which will require that setup can just import the class and call the functions.

</details>

<details>
<summary> Modul-2-CI-CD-Devops</summary>

## Reflection 1

1. List the code quality issue(s) that you fixed during the exercise and explain your strategy
on fixing them.

There are multiple issues identified in SonarQube, however I decided to fix only the high, medium, as well as unused imports issues

![alt text](<assets/Screenshot 2026-02-21 182500.png>)

![alt text](assets/high.png)

All of the high severity are string literal duplications. To fix it, I assign the string literal that is being duplicated to a variable and then call that variable whenever I need to use it. Here is an example: 

![alt text](assets/fix-high.png)

For unused imports, the fix is obvious, remove it

![alt text](assets/import.png)

Next I refactored the `ProductControllerTest` from 3 seperate test cases to 1 parameterized testcase. For this I used `@ParameterizedTest`, ` @NullAndEmptySource`, and `@ValueSource`

![alt text](assets/unit-test.png)

Next on `DeleteProductFunctionalTest` and `UpdateProductFunctionalTest` there is a catch exception block that is empty. I added a comment explaining why it is empty

![alt text](assets/empty.png)

Next I added a private constructor to `ProductValidator` to prevent instantiation as it is a utility class

![alt text](assets/private.png)

Lastly I removed feild injection and used constructor injection instead for `ProductServiceImpl` and `ProductController`

![alt text](assets/inject.png)

As we can see, after all the changes there are no more High and Medium severity issues

![alt text](assets/image.png)

2. Look at your CI/CD workflows (GitHub)/pipelines (GitLab). Do you think the current
implementation has met the definition of Continuous Integration and Continuous
Deployment? Explain the reasons (minimum 3 sentences)!

Yes, I do think that my current implementation has met the definition of Continous Integration and Continous Deployment. The definition of Continous Integration is the practice were we automate the process of integrating changes by utilizing tools. Wheneve we want to integrate changes, we need to test the code first. My current workflow has automated 3 tests which will be activated everytime I push changes, OSSF Scorecard, unit tests, and sonarcloud for tests coverage. There is also a check for SonarQube Cloud code scan however it only applies to the master branch as I only have the free tier.

The definition of Continous Deployment is automating deployment after our code has past the tests defined in the CI workflow. My deploy script does exactly that, it checks for the status of all the workflows (sonar, ossf, and unit tests) and make sure that it all succeeds. If it doesnt, then the deploy job would be skipped. However if it all succeeds then it will automatically deploy the application to koyeb utilizing the koyeb cli.

For those reasons, I believe that my implementation has met the definition of CI/CD, although there are definitely some room for improvements, for example integrating functional tests to the workflow instead of only unit tests.
</details>

<details>
<summary> Modul 3 Maintanability and OO principles</summary>

> 1. Explain what principles you apply to your project!
   
I applied the following principles
1. **Single Responsibility Principle** : Refactoring the controller and splitting it into two files, 1 for handling Car and one more for Product, this way each class only handles one responsibility
2. **Open/Closed Principle** : Adding a repository interface which uses generic types which is then implemented in each of the repository, in this case product and car. That way if we want to create a new repository we can do it without having to modify the existing code. 
3. **Dependency Inversion Principle** : Changing the variable type of carservice in car controller from CarServiceImpl to CarService because CarController should depend on abstraction instead of implementation
4. **Interface Segregation Principle** : Splitting up the repository interface into 2 interface where one is used for reading and one for writeing. This way, it will allow us to create a new repository that could only read/write
5. **Liskov Substitution Principle** : The codebase has implemented Liskov Substitution Principle without needing further modification. For example any code using the RepositoryInterface can accept ProductRepository without behavioral changes.

> 2.  Explain the advantages of applying SOLID principles to your project with examples.

1. Increase code flexibility
SOLID principles such as OCP and ISP help increase code flexibility. In this case, we have the flexibility to create a repository that could only read/write without having to modify any of the existing code, we just need to implement the suitable interface
2. Increase code maintanability
SOLID principles such as the SRP help increase code mintanability. For example a developer looking to add new endpoints/fix bugs for the car controller don't have to worry about accidentally introducing bugs to product controller. This can also help to reduce git merge conflicts in the case where 1 developer is working on car controller and another on product controller. If both controllers are implemented in the same file, there would be a lot of merge conflicts.
3. Increase code readability
SOLID principles help make our code more readable. For example, in this code base every classes are named appropriately and every function in each class isn't too long. Principles such as SRP allow us to know which class implements a certain functionality without even needing to read the class as each class has desccriptive names and only handles one responsibility

> 3.  Explain the disadvantages of not applying SOLID principles to your project with examples.

1. Harder to do code refactoring
For example if the controllers are not split, 1 single change to the code for the controller that handles cars could effect the controller that handles product. This will increase the risk of introducing bugs in other places where it could've easily been avoided

2. Harder to expand code
Say you want to add a new repository whose sole purpose is to read data. If we do not split the repository interface then that repository would be forced to implement unnecessary functions (e.g edit,create,delete). This would make it harder to expand the functionality as we need to think of what to do with those unnecessary functions. It would also make our code harder to read as there are more functions
</details>

<details>
<summary>Modul 4 Refactoring & TDD</summary>

> 1. Reflect based on Percival (2017) proposed self-reflective questions (in “Principles and Best Practice of Testing” submodule, chapter “Evaluating Your Testing Objectives”), whether this
TDD flow is useful enough for you or not. If not, explain things that you need to do next time
you make more tests.

Menurut saya TDD workflow yang saya lakukan di modul kali ini sangat membantu saya. Karena sudah adanya test cases sejak awal, saya dapat dengan mudah test fitur yang saya develop apakah ada bug atau tidak. Saya tidak perlu manual mencoba untuk run programmya lagi coba-coba fitur tersebut. Selain itu, dalam workflow ini saya juga mengintegrasikan functional testing untuk setiap fitur sehingga saya lebih yakin bahwa fitur saya bekerja dengan semestinya. Test coverage dari program ini juga sudah 100% sehingga akan membantu meminimalisir kemungkinan edge-cases yang terlewat.

Namun saya menyadari bahwa tests saya saat ini sangat minim dokumentasi. Hal ini dapat mempersulit jika ada keperluan refactoring tests ataupun debugging tentang mengapa sebuah test gagaol ketika ditambah fitur baru. Maka sebagai peningkatan, saya perlu mendokumentasikan setiap unit tests dan functional tests agar lebih maintainable kedepannya. Selain itu workflow saya saat ini belum terlalu linear, kadang saya membuat unit tests untuk fitur yang lain sementara fitur yang saya sedang kerjakan belum terimplementasi dengan baik. Kedepannya saya akan memperbaiki workflow saya sehingga TDD yang dilakukan dapat memberikan manfaat yang lebih banyak.

> 2. You have created unit tests in Tutorial. Now reflect whether your tests have successfully
followed F.I.R.S.T. principle or not. If not, explain things that you need to do the next time you
create more tests.

Menurut saya unit test yang saya buat sudah memenuhi F.I.R.S.T principle. Pertama dari segi Fast, unit tests saya berjalan dengan cepat karena testnya hanya berfokus pada unit kecil dalam kode saya. Dengan ini saya dapat mengurangi waktu menunggu tests berjalan ketika development. Lalu dari segi Independent, semua unit tests saya dapat berjalan dengan baik tanpa perlunya class unit tests lainnya.

Dari segi Repeatable dan Self validating, saya sudah menjalankan unit tests saya berkali-kali dan hasilnya selalu konsisten antar run. Selain itu unit tests yang saya buat juga menunjukkan hasil passed or failed dengan jelas sehingga saya tidak perlu cek secara mandiri. Terakhir dari segi timely sebagian besar dari unit tests saya sudah mengikuti workflow TDD dan mengcover semua happy dan unhappy paths. Namun untuk functional tests saya, terkadang saya melewatkan beberapa unhappy paths yang baru saya sadari ketika fitur sudah diimplementasi dan saya cek code coveragenya. Sehingga dari aspek tersebut masih dapat saya berbaiki dan tingkatkan.

</details>