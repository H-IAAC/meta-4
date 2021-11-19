#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Thu Nov 18 10:40:04 2021

@author: amparomunoz
"""
import pandas as pd
#@title Default title text


class DataHar:  
  def __init__(self):
      self.df_train = pd.read_csv("../datasets_data/data-har/datacsv/datahar_train.csv")  
      self.df_test = pd.read_csv("../datasets_data/data-har/datacsv/datahar_test.csv")   
      
  def get_pd_train(self):
      return self.df_train 
  
  def get_pd_test (self):
      return self.df_test
  
 
      
  def get_data_all(self):
    return self.df

  def get_data_by_user(self,df,uuid):
    
    return df.loc[df['user']==int(uuid)]
      
  def get_labels_name(self):
    return ["label:downstairs","label:upstairs", "label:sitting",    "label:standing",  "label:walking",    "label:jogging"]
  #.split("label:")[1]


  def get_sensors_names_raw_accelerometer_basic(self):
    return  ["userAcceleration.x",	"userAcceleration.y",	"userAcceleration.z"]

  def get_sensors_names_proc_rotationRate_basic(self):
    return ["rotationRate.x",	"rotationRate.y",	"rotationRate.z"]

  def get_sensors_names_proc_gravity_basic(self):
    return ["gravity.x",	"gravity.y"	,"gravity.z"]


  def get_sensors_names_raw_attitude_basic(self):
    return ["attitude.roll",	"attitude.pitch",	"attitude.yaw"]

    
  def get_sensors_names(self):
    return  ["userAcceleration.x",	"userAcceleration.y",	"userAcceleration.z","rotationRate.x",	"rotationRate.y",	"rotationRate.z","gravity.x",	"gravity.y"	,"gravity.z","attitude.roll",	"attitude.pitch",	"attitude.yaw"]

  def get_users_names(self):
    return [1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24]
    

  def get_data_X_Y(self,df,sensors,labels,timestamp=False):
    sensors_to_display=sensors
    print("sensors", sensors_to_display)
    print(labels)
    X1,targets1 =self.get_sensors_values(df,sensors_to_display,labels)
    if(len(targets1)>0):
      print("X shape",X1.shape ,"target shape",targets1.shape) 
    else:
      print("nao tem dados")
    return X1,targets1

  def get_sensors_values(self,df,sensors,labels):
    dfs=[]
    
    for label in labels:
      activity_values = df[df[label] == 1][sensors]
      activity_values['y']=label
     
      if(activity_values.shape[0]>1):
        print(label,activity_values.shape)
        dfs.append(activity_values)    
    if(len(dfs)>0):
      df_all=pd.concat(dfs)
      X=df_all.drop(columns=['y'])
      #print(X)
      targets=df_all['y']
    else:
      X=[]
      targets=[]
    return X,targets

