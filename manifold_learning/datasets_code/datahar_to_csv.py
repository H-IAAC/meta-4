#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Wed Nov 17 21:40:48 2021

@author: amparomunoz
"""

import numpy as np
import pandas as pd
##_____________________________

def get_ds_infos():
    ## 0:Code, 1:Weight, 2:Height, 3:Age, 4:Gender
    dss = np.genfromtxt("../datasets_data/data-har/motion-sense_gitlab/data/data_subjects_info.csv",delimiter=',')
    dss = dss[1:]
    print("----> Data subjects information is imported.")
    return dss
##____________

def creat_time_series(num_features, num_act_labels, num_gen_labels, label_codes, trial_codes):
    dataset_columns = num_features+num_act_labels+num_gen_labels
    ds_list = get_ds_infos()
    train_data = np.zeros((0,dataset_columns))
    test_data = np.zeros((0,dataset_columns))
    for i, sub_id in enumerate(ds_list[:,0]):
        for j, act in enumerate(label_codes):
            for trial in trial_codes[act]:
                #print(act)
                fname = '../datasets_data/data-har/A_DeviceMotion_data/'+act+'_'+str(trial)+'/sub_'+str(int(sub_id))+'.csv'
                raw_data = pd.read_csv(fname)
                raw_data = raw_data.drop(['Unnamed: 0'], axis=1)
                unlabel_data = raw_data.values
                label_data = np.zeros((len(unlabel_data), dataset_columns))
                label_data[:,:-(num_act_labels + num_gen_labels)] = unlabel_data
                label_data[:,label_codes[act]] = 1
                label_data[:,-(num_gen_labels)] = str(int(sub_id))#int(ds_list[i,4])
                #print(fname,int(sub_id),int(ds_list[i,4]))
                ## We consider long trials as training dataset and short trials as test dataset
                if trial > 10:
                    test_data = np.append(test_data, label_data, axis = 0)
                else:    
                    train_data = np.append(train_data, label_data, axis = 0)
    return train_data , test_data
#________________________________

def load_DATA_HAR():
  print("--> Start...")
  ## Here we set parameter to build labeld time-series from dataset of "(A)DeviceMotion_data"
  num_features = 12 # attitude(roll, pitch, yaw); gravity(x, y, z); rotationRate(x, y, z); userAcceleration(x,y,z)
  num_act_labels = 6 # dws, ups, wlk, jog, sit, std
  num_gen_labels = 1 # 0/1(female/male)
  label_codes = {"dws":num_features, "ups":num_features+1, "wlk":num_features+2, "jog":num_features+3, "sit":num_features+4, "std":num_features+5}
  trial_codes = {"dws":[1,2,11], "ups":[3,4,12], "wlk":[7,8,15], "jog":[9,16], "sit":[5,13], "std":[6,14]}    
  ## Calling 'creat_time_series()' to build time-series
  print("--> Building Training and Test Datasets...")
  train_ts, test_ts = creat_time_series(num_features, num_act_labels, num_gen_labels, label_codes, trial_codes)
  print("--> Shape of Training Time-Seires:", train_ts.shape)
  print("--> Shape of Test Time-Series:", test_ts.shape)
  
  col=["attitude.roll",	"attitude.pitch",	"attitude.yaw",	"gravity.x",	"gravity.y"	,"gravity.z",	"rotationRate.x",	"rotationRate.y",	"rotationRate.z",	"userAcceleration.x",	"userAcceleration.y",	"userAcceleration.z","label:downstairs","label:upstairs", "label:sitting",    "label:standing",  "label:walking",    "label:jogging","user"]
  df_data_Har_train=pd.DataFrame(train_ts,columns=col)
  df_data_Har_test=pd.DataFrame(test_ts,columns=col)
  return df_data_Har_train,df_data_Har_test



df_data_Har_train,df_data_Har_test=load_DATA_HAR()
df_data_Har_train.to_csv("../datasets_data/data-har/datacsv/datahar_train.csv")
df_data_Har_test.to_csv("../datasets_data/data-har/datacsv/datahar_test.csv")




