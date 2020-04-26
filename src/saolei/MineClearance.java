package saolei;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
 
public class MineClearance {
    static private int midtime = 3600,mineNum = 0;/* ����ʱʱ���Լ����������� */
    private static ImageIcon face = new ImageIcon("src/images/face.jpg");/* С����ͼ�� */
    static private JLabel label1,label2;/* ��ʾ���� */
    static private GamePanel gp;/* ���� */
 
    MineClearance(){
        /* ���ƴ��� */
        JFrame f = new JFrame("ɨ��");
        f.setBounds(600,200,500,600);
        f.setDefaultCloseOperation(3);
        f.setLayout(null);
        label1 = new JLabel("ʣ��ʱ�䣺" +(midtime / 60 / 60 % 60) + ":"+ (midtime / 60 % 60)+ ":" +(midtime % 60));
        label1.setBounds(10,20,120,20);
        f.add(label1);
        /* ��ʾ������ */
        label2 = new JLabel("ʣ��:"+mineNum);
        label2.setBounds(400,20,120,20);
        f.add(label2);
        /* ���ð�ť */
        JButton bt = new JButton(face);
        bt.setBounds(230, 15,30,30);
        bt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                f.dispose();
                midtime = 3600;
                new MineClearance();
            }
        });
        f.add(bt);
        /* �������� */
        gp = new GamePanel(20,20);
        gp.setBounds(40,100,400,400);
        f.add(gp);
        /* ��ʾ���� */
        f.setVisible(true);
    }
    /* ����ʱ�߳� */
    static class CountDown extends Thread{
        public void run(){
            while (midtime > 0){
                try{
                    -- midtime;
                    label1.setText("ʣ��ʱ�䣺" +(midtime / 60 / 60 % 60) + ":"+ (midtime / 60 % 60)+ ":" +(midtime % 60));
                    this.sleep(1000);
                }catch (Exception e){
                    System.out.println("����" + e.toString());
                }
            }
            if(midtime == 0) {
                gp.showBomb();
                JOptionPane.showMessageDialog(null,"ʱ���ѵ�","��Ϸ����",JOptionPane.PLAIN_MESSAGE);
            }
        }
 
    }
    public static void main(String[] args){
       new MineClearance();
       /* ����ʱ */
       CountDown cd = new CountDown();
       cd.start();
    }
    /* �޸������� */
    public static void setMineNum(int i){
        mineNum = i;
        label2.setText("ʣ��:"+mineNum);
    }
}
 
class GamePanel extends JPanel {
    private int rows, cols, bombCount,flagNum;
    private final int BLOCKWIDTH = 20;
    private final int BLOCKHEIGHT = 20;
    private JLabel[][] label;
    private boolean[][] state;
    private Btn[][] btns;
    private byte[][] click;
    private static ImageIcon flag = new ImageIcon("src/images/flag.jpg");
    public ImageIcon bomb = new ImageIcon("src/images/bomb.jpg");
    private static ImageIcon face = new ImageIcon("src/images/face.jpg");
    /* �������� */
    public GamePanel(int row, int col) {
        rows = row;/* ���� */
        cols = col;/* ���� */
        bombCount = rows * cols / 10; /* ������ */
        flagNum = bombCount;/* ����������ڲ��죩 */
        label = new JLabel[rows][cols];
        state = new boolean[rows][cols];/* ���ڴ洢�Ƿ��е��� */
        btns = new Btn[rows][cols];
        click = new byte[rows][cols];/* ���ڴ洢��ť���״̬��0-δ�����1-�ѵ����2-δ�������Χ���ף�3-���죩 */
        MineClearance.setMineNum(flagNum);
        setLayout(null);
        initLable();
        randomBomb();
        writeNumber();
        randomBtn();
    }
 
    public void initLable() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                JLabel l = new JLabel("", JLabel.CENTER);
                // ����ÿ��С����ı߽�
                l.setBounds(j * BLOCKWIDTH, i * BLOCKHEIGHT, BLOCKWIDTH, BLOCKHEIGHT);
                // ���Ʒ���߿�
                l.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                // ���÷���Ϊ͸��,�������������ɫ
                l.setOpaque(true);
                // �������Ϊ��ɫ
                l.setBackground(Color.lightGray);
                // ��������뵽������(�����JPanel)
                this.add(l);
                // ������浽�������,���㹫��
                label[i][j] = l;
                label[i][j].setVisible(false);
            }
        }
    }
 
    /* ���Ƶ��� */
    private void randomBomb() {
        for (int i = 0; i < bombCount; i++) {
            int rRow = (int) (Math.random() * rows);
            int rCol = (int) (Math.random() * cols);
            label[rRow][rCol].setIcon(bomb);
            state[rRow][rCol] = true;/* �е��׵ĸ���stateΪ�� */
        }
    }
 
    /* �������� */
    private void writeNumber() {
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j) {
                if (state[i][j]) {
                    continue;
                }
                int bombCount = 0;
                /* Ѱ�����Լ�Ϊ���ĵľŸ������еĵ����� */
                for (int r = -1; (r + i < rows) && (r < 2); ++r) {
                    if (r + i < 0) continue;
                    for (int c = -1; (c + j < cols) && (c < 2); ++c) {
                        if (c + j < 0) continue;
                        if (state[r + i][c + j]) ++bombCount;
                    }
                }
                if (bombCount > 0) {
                    click[i][j] = 2;
                    label[i][j].setText(String.valueOf(bombCount));
                }
            }
        }
    }
    /* ���ư�ť */
    private void randomBtn() {
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j) {
                Btn btn = new Btn();
                btn.i = i;
                btn.j = j;
                btn.setBounds(j * BLOCKWIDTH, i * BLOCKHEIGHT, BLOCKWIDTH, BLOCKHEIGHT);
                this.add(btn);
                btns[i][j] = btn;
                btn.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        /* ������ */
                        if(e.getButton() == MouseEvent.BUTTON1) open(btn);
                        /* �Ҽ���� */
                        if(e.getButton() == MouseEvent.BUTTON3) placeFlag(btn);
                    }
 
                }
                );
 
            }
        }
 
    }
    /* ��������� */
    private void open(Btn b){
        /* ���� */
        if(state[b.i][b.j]){
            for (int r = 0;r < rows;++r){
                for(int c = 0;c < cols; ++c){
                    btns[r][c].setVisible(false);/* ����label */
                    label[r][c].setVisible(true);/* ��ʾ��ť������ֻ�������˰�ť������ʾ��ť�����label�� */
                }
            }
            JOptionPane.showMessageDialog(null,"��ʧ����","��Ϸ����",JOptionPane.PLAIN_MESSAGE);
        }else /* û�в��� */{
            subopen(b);
        }
    }
    /* �ݹ���ܱ����� */
    private void subopen(Btn b){
        /* ���ף����ܴ� */
        if(state[b.i][b.j]) return;
        /* �򿪹��ĺͲ���ģ����ô� */
        if(click[b.i][b.j] == 1 || click[b.i][b.j] == 4) return;
        /* ��Χ���׵ģ�ֻ���� */
        if(click[b.i][b.j] == 2) {
            b.setVisible(false);
            label[b.i][b.j].setVisible(true);
            click[b.i][b.j] = 1;
            return;
        }
        /* �򿪵�ǰ�����ť */
        b.setVisible(false);
        label[b.i][b.j].setVisible(true);
        click[b.i][b.j] = 1;
        /* �ݹ����ܱ߰˸���ť */
        for (int r = -1; (r + b.i < rows) && (r < 2); ++r) {
            if (r + b.i < 0) continue;
            for (int c = -1; (c + b.j < cols) && (c < 2); ++c) {
                if (c + b.j < 0) continue;
                if (r==0 && c==0) continue;
                Btn newbtn = btns[r + b.i][c + b.j];
                subopen(newbtn);
            }
        }
    }
    /* ���� */
    private void placeFlag(Btn b){
        /* ֻ�ܲ�͵�������ͬ��Ŀ������ */
        if(flagNum>0){
            /* �����ģ��ٵ�һ��ȡ�� */
            if(click[b.i][b.j] == 3){
                if(label[b.i][b.j].getText() == "[0-9]") click[b.i][b.j] = 2;
                else click[b.i][b.j] = 0;
                b.setIcon(face);
                ++ flagNum;
                MineClearance.setMineNum(flagNum);
            }else /* δ����ģ����� */{
                b.setIcon(flag);
                click[b.i][b.j] = 3;
                -- flagNum;
                MineClearance.setMineNum(flagNum);
            }
            /* ���������Ӳ����ˣ�����Ƿ�ɹ� */
            if(flagNum == 0){
                boolean flagstate = true;
                for(int i = 0;i < rows; ++i){
                    for(int j = 0;j < cols; ++j){
                        if (click[i][j] != 3 && state[i][j]) flagstate = false;
                    }
                }
                if(flagstate) JOptionPane.showMessageDialog(null,"���ɹ���","��Ϸ����",JOptionPane.PLAIN_MESSAGE);
            }
        }else /* ���������ˣ����ܲ� */{
            JOptionPane.showMessageDialog(null,"������þ�","�������",JOptionPane.PLAIN_MESSAGE);
        }
    }
    /* ��ʾ���� */
    public void showBomb(){
        for (int r = 0;r < rows;++r){
            for(int c = 0;c < cols; ++c){
                btns[r][c].setVisible(false);/* ����label */
                label[r][c].setVisible(true);/* ��ʾ��ť������ֻ�������˰�ť������ʾ��ť�����label�� */
            }
        }
    }
}
 
class Btn extends JButton{
    public int i,j;
}

