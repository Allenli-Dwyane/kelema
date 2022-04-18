#ifndef GETACC_H_
#define GETACC_H_
class Accelerator{
  public:
  // get horizontal accelerator
  float get_acc(); 
  // run the accelerator mode
  void run();
  private:
  int32_t accelerometer[3];
}
#endif
