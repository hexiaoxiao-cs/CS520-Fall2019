import numpy as np


def sigmoid(Z):
    return 1 / (1 + np.exp(-Z))

def sigmoid_backward(dA, Z):
    sig = sigmoid(Z)
    return dA * sig * (1 - sig)

class Dense_layer:
    def __init__(self, input_units, output_units, learning_rate=0.5):
        # f(x) = Weights*Inputs + Some constants
        self.weights = np.random.normal(loc=0.0,scale=np.sqrt(2 / (input_units + output_units)),size=(input_units, output_units))
        #print(1)
        self.biases = np.zeros(output_units) #W[0]
        #print(1)
        self.learning_rate = learning_rate
        #print(1)
        
    def forward(self, inputs):
        # f(x) = Weights*Inputs + Some constants

        # input shape: [batch, input_units]
        # output shape: [batch, output units]
        self.layer_input = inputs
        return np.dot(inputs, self.weights) + self.biases

    def backward(self, grad_output):

        W = self.weights

        # Calculate gradient w.r.t layer weights
        grad_w = self.layer_input.T.dot(grad_output)
        grad_w0 = np.sum(grad_output, axis=0, keepdims=True)

        # Update the layer weights
        self.weights = self.weights - self.learning_rate * grad_w
        self.biases = self.biases - self.learning_rate * grad_w0

        # Return accumulated gradient for next layer
        # Calculated based on the weights used during the forward pass
        accum_grad = grad_output.dot(W.T)
        return accum_grad