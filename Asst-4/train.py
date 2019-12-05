import imageio
import numpy as np
from glob import glob
import os
from datetime import datetime
import tqdm
import random
import pickle
import modules

training_folder="./training"
now = datetime.now()

def read_images():
    x_truth=[]
    y_truth=[]
    file_order=[]
    print("Reading Images\n")
    for image in tqdm.tqdm(glob(training_folder+"/*.jpg")):
        basename=os.path.basename(image)
        filename=os.path.split(basename)[0]
        x_truth.append(imageio.imread(training_folder+filename+"_bw.jpeg"))
        y_truth.append(imageio.imread(image))
        file_order.append(filename)
    return x_truth, y_truth, file_order

def generate_validate_set(size, prob=0.1):
    validation_list=random.sample(range(size),int(size*prob*2))
    training_list = [x for x in list(range(validation_list.size)) if x not in validation_list]
    test_list=validation_list[:len(validation_list)//2]
    validation_list = validation_list[len(validation_list) // 2:]

    return training_list,validation_list,test_list


def write_current_configuration(validation_list,test_list,file_order):
    pickle.dump(validation_list,open("validate_list_"+str(now)+".p", "wb"))
    pickle.dump(test_list, open("test_list_" + str(now) + ".p", "wb"))
    pickle.dump(file_order, open("file_order" + str(now) + ".p", "wb"))

def normalize_image(x_image,y_image): #normalize to -0.5,0.5
    x_image=x_image/256
    y_image=y_image/256
    return x_image,y_image

def prepare_data(x_truth,y_truth,training_list,validation_list,test_list):
    x_data=[]
    y_data=[]
    x_validation_data=[]
    y_validation_data = []
    x_test_data=[]
    y_test_data = []
    for i in range(x_truth.size):
        if i in training_list:
            x_data.append(np.array(x_truth[i]).flatten())
            x_data.append(np.array(y_truth[i]))
        else:
            if i in validation_list:
                x_validation_data.append(np.array(x_truth[i]).flatten())
                y_validation_data.append(np.array(y_truth[i]))
            else:
                x_test_data.append(np.array(x_truth[i]).flatten())
                y_test_data.append(np.array(y_truth[i]))
    return x_data,y_data,x_validation_data,y_validation_data,x_test_data,y_test_data


def forward_pro(x_data,networks):
    for layer in networks:
        if(networks[0]==layer):
            lo=layer.forward(x_data)
        else:
            layer.forward(prev)
        prev=modules.sigmoid(lo)
    return prev


def begin_training(x_data,y_data,x_validation_data,y_validation_data,x_test_data,y_test_data):
    networks=[]
    basesize = x_data[0].size
    networks.append(modules.Dense_layer(basesize,basesize,learning_rate=0.5))
    networks.append( modules.Dense_layer(basesize, basesize*2,learning_rate=0.5))
    networks.append(modules.Dense_layer(basesize*2,basesize*3,learning_rate=0.5))

    y_pred=forward_pro(x_data,networks)
    y_pred_image=y_pred.reshape(255,255,3)
    MSE = np.square(np.subtract(y_data, y_pred_image)).mean()

    #Start backward propogation

    #calculate the derivative

    dA_prev = - (np.divide(Y_data, Y_hat) - np.divide(1 - Y, 1 - Y_hat));

    #try 1-> Linearize and use 4 dense layer each with 3 densely connected layers
