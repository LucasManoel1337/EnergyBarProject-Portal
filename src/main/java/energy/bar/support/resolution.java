/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package energy.bar.support;

public class resolution {

    int desiredWidth = 1000;
    int desiredHeight = 600;

// Função para calcular a posição X proporcional
    public int getProportionalX(int originalX, int originalWidth) {
        return (int) ((double) originalX / 1000 * desiredWidth);
    }

// Função para calcular a posição Y proporcional
    public int getProportionalY(int originalY, int originalHeight) {
        return (int) ((double) originalY / 600 * desiredHeight);
    }

// Função para calcular a largura proporcional
    public int getProportionalWidth(int originalWidth, int originalTotalWidth) {
        return (int) ((double) originalWidth / 1000 * desiredWidth);
    }

// Função para calcular a altura proporcional
    public int getProportionalHeight(int originalHeight, int originalTotalHeight) {
        return (int) ((double) originalHeight / 600 * desiredHeight);
    }

}
