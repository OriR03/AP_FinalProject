# Java Computational Graph Server

---

## Installation

Download the branch as a ZIP file and open the project using a Java IDE.  
To run the server, build the project starting from:

```
/src/biu_project/Main.java
```

---

## Client Side

Open any browser and navigate to the address:

```
localhost:8080/app
```

---

## Configuration File

The configuration file defines the structure and computation type of the graph.

There are two kinds of agents:

- **String Agents:**  
  `UpperCase`, `LowerCase`, `Reverse`, `Concat`

- **Numeric Agents (Int/Double):**  
  `Mul`, `Inc`, `Plus`, `Square`

Agents can be either **binary** or **unary**:

- **Binary Agents:**  
  `Mul`, `Plus`, `Concat`

- **Unary Agents:**  
  `UpperCase`, `LowerCase`, `Reverse`, `Inc`, `Square`

---

To write the config file - 
Write the desired agent in this form - biu_project.configs.__agent__
After a breakline (\n) write the input topic or topics 
After another breakline write the output topic, which the calculation of the agent will be inserted into

---

### Example Configuration File

```
biu_project.configs.ConcatAgent
A,B
C
biu_project.configs.LowerCaseAgent
C
D
biu_project.configs.ReverseAgent
D
E
```

This will construct a computational graph where if the input was - A=ABC,B=CDE Then 

a|b->c=ABCCDE   

C↓->D=abccde  

D↺->E=edccba

More example graphs are under `/src/config_files`

