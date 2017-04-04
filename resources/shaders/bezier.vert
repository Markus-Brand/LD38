layout (location = 0) in float progress;

out VS_OUT {
    float progress;
} vs_out;

void main(){ 
	vs_out.progress = progress;
}