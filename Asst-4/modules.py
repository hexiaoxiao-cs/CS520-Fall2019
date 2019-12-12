import numpy as np


def sigmoid(Z):
    return 1 / (1 + np.exp(-Z))

def sigmoid_backward(dA, Z):
    sig = sigmoid(Z)
    return dA * sig * (1 - sig)

def relu(Z):
    return np.maximum(0,Z)

def relu_backward(dA, Z):
    dZ = np.array(dA, copy = True)
    dZ[Z <= 0] = 0
    return dZ

class Dense_layer:
    def __init__(self, input_units, output_units, learning_rate=0.5):
        # f(x) = Weights*Inputs + Some constants
        self.weights = np.random.normal(loc=0.0,scale=np.sqrt(2 / (input_units + output_units)),size=(input_units, output_units))
        #print("initialized Weights")
        #print(self.weights)
	    #print(1)
        self.biases = np.zeros(output_units) #W[0]
        self.biases=self.biases.reshape((self.biases.size,1))
        #print(1)
        self.learning_rate = learning_rate
        #print(1)
        
    def forward(self, inputs):
        # f(x) = Weights*Inputs + Some constants

        # input shape: [batch, input_units]
        # output shape: [batch, output units]
        self.layer_input = inputs
        return np.dot(self.weights, inputs) + self.biases

    def backward(self, grad_output):
        #print("Our Output is")
        grad_input = np.dot(self.weights,grad_output)
        #print(grad_input)
        # compute gradient w.r.t. weights and biases
        # print("layer_input")
        # print(self.layer_input)
        # print("grad_output")
        # print(grad_output)
        # print("grad_weights")
        grad_weights = np.dot(grad_output,self.layer_input.T)
        # print(grad_weights)
        # print("grad_biases")
        grad_biases = grad_output.mean()
        # print(grad_biases)
        # Update the layer weights
        self.weights = self.weights - self.learning_rate * grad_weights
        self.biases = self.biases - self.learning_rate * grad_biases


        return grad_input
