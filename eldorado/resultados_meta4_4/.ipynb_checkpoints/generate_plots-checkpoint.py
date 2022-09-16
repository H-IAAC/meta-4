import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt
times=[]
accu=[]
data=[]
def get_data_exp(exp_n):
  
    df = pd.read_csv('Extrasensory/client0/Extrasensory_umap_10.txt',sep=';')
    keys = ['timestamp','loss','Acc']
    df.columns = [x for x in keys]
    print(df['loss']) 

def getValues(fileName):
    with open(fileName + '.txt') as f:
      lines = f.readlines()
    

  
    line_count=0  
    count_round = 0
    for line in lines: 
        #print(line)
        if(line_count>0) :
            print(line)
            
            data1=line.split(";")

            resultsj=[data1[0],data1[1],data1[2]]
            data.append(resultsj)
            print(data1[2])
        line_count=line_count+1
    
getValues('Extrasensory/client0/Extrasensory_umap_10')
#print(data)
df = pd.DataFrame(data,columns = ['timestamp','loss','Acc'])


fig = plt.figure()
ax = plt.axes()

x = np.linspace(0, 10, 1000)
ax.plot(df['timestamp'],df['Acc'] );