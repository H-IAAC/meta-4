#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Thu Nov 18 10:40:04 2021

@author: amparomunoz
"""

import sys
sys.path.append('../')
import pandas as pd
#@title Default title text

    
import pandas as pd
from datahar import DataHar    
    

Datahar= DataHar()



## obtem os dados de teste
pd_test=Datahar.get_pd_test()

## obtem os dados de traino
pd_train=Datahar.get_pd_train()

## obtem os dados para o usuario 1
pd_train_user1_user=Datahar.get_data_by_user(pd_train,"1")
print(pd_train_user1_user)

## obtem os nomes de todos os labels
labels_name=Datahar.get_labels_name()
    
### obtem os nomes dos usuarios
users_name=Datahar.get_users_names()


#obtem os nomes dos sensores do acelerometro
acelerometer_names=Datahar.get_sensors_names_raw_accelerometer_basic()
print(acelerometer_names)

print(Datahar.get_sensors_names())