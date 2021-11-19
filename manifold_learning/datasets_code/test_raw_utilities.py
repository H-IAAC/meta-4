#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Thu Nov 18 12:03:44 2021

@author: amparomunoz
"""

import sys
sys.path.append('../') 

    
import pandas as pd
from datahar import DataHar    
    
from raw_utilities import RAW_PLOTS    
Datahar= DataHar()
Raw_plots=RAW_PLOTS ()
df_user=Datahar.get_data_by_user(Datahar.get_pd_test (), "1")

Raw_plots.plot_by_size_features(Datahar.get_pd_test (),Datahar.get_labels_name())    

    