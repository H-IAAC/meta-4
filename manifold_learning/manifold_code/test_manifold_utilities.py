#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Thu Nov 18 10:55:19 2021

@author: amparomunoz
"""

import sys
sys.path.append('../')

#### DATASET
from datasets_code.datahar import DataHar 
Datahar= DataHar()
## obtem os dados de traino
pd_train=Datahar.get_pd_test()
## obtem os dados para o usuario 1
pd_train_user1_user=Datahar.get_data_by_user(pd_train,"1")
sensors=Datahar.get_sensors_names()
labels=Datahar.get_labels_name()
X,targets=Datahar.get_data_X_Y(pd_train_user1_user,sensors,labels,timestamp=False)


 
from manifold_learning import MANIFOLD_LEARNING
Manifold_learning= MANIFOLD_LEARNING() 

parameters={
'isomap':{
    'n_components':2,
    'n_neighbors':10
  } ,
'lle':{
    'n_components':2,
    'n_neighbors':10
  }  ,
'tsne':{
    'n_components':2,
    'n_neighbors':20
  } 
}

       
        
model_tsne =Manifold_learning.tsne(parameters["tsne"])
projections = model_tsne.fit_transform(X)



from manifold_utilities import MANIFOLD_UTILITIES
Manifold_utilities=MANIFOLD_UTILITIES()
  
Manifold_utilities.plot2d_scatter_interactive(projections,targets,"tsne,usuario>1")
